package com.megustav.revolut.rest.processor;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.megustav.revolut.JerseyConfig;
import com.megustav.revolut.data.OperationType;
import com.megustav.revolut.database.dao.AccountsDao;
import com.megustav.revolut.database.entity.InternalAccount;
import com.megustav.revolut.misc.BlockingService;
import com.megustav.revolut.rest.data.OperationPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

/**
 * Operation processor.
 *
 * Operation processing is pretty heavy, so extracting the logic
 * into separate class.
 *
 * Ideally ALL business logic should be separated from handlers,
 * but then (in this case) there is a need of new layer of
 * business responses, because these entities (called, say, services)
 * should not even know about REST.
 *
 * If I had more time I could've created this layer.
 * So for now there is this class encapsulating operation business logic
 * that knows about the fact that it is used in a REST environment
 * ({@link Response} return type)
 *
 * @author MeGustav
 * 21/05/2018 22:56
 */
@Singleton
public class OperationProcessor {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(JerseyConfig.class);
    /** Accounts DAO */
    private final AccountsDao dao;
    /** Blocking service */
    private final BlockingService blockingService;

    @Inject
    public OperationProcessor(AccountsDao dao, BlockingService blockingService) {
        this.dao = dao;
        this.blockingService = blockingService;
    }

    /**
     * Process the operation
     *
     * @param accountNumber account number
     * @param operation operation data
     * @return response
     */
    public Response processOperation(String accountNumber, OperationPayload operation) {
        BigDecimal amount = operation.getAmount();
        if (amount == null || BigDecimal.ZERO.compareTo(amount) > 0) {
            log.debug("Invalid amount: {}", amount);
            throw new BadRequestException("Invalid amount");
        }

        // Locking account for operation
        Lock lock = blockingService.getLock(accountNumber);
        try {
            lock.lock();
            InternalAccount account = fetchAccount(accountNumber);
            if (account.getCurrency() != operation.getCurrency()) {
                log.debug("Operation and account currencies differ. Account: {}, operation: {}",
                        account.getCurrency(), operation.getCurrency());
                throw new BadRequestException("Operation and account currencies differ");
            }
            return performOperation(account, operation.getType(), operation.getAmount());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Perform an operation
     *
     * @param account account
     * @param type operation type
     * @param amount operation amount
     * @return response
     */
    private Response performOperation(InternalAccount account, OperationType type, BigDecimal amount) {
        if (type == OperationType.WITHDRAWAL) {
            BigDecimal remainder = account.getBalance().subtract(amount);
            if (BigDecimal.ZERO.compareTo(remainder) > 0) {
                log.debug("Insufficient funds on account '{}' for withdrawal of {}",
                        account.getNumber(), amount
                );
                throw new ClientErrorException("Insufficient funds", Response.Status.CONFLICT);
            }
        }
        // Executing account modification and operation insertion in transaction
        return dao.executeInTransaction(handle -> {
            boolean operationInserted = dao.insertOperation(handle, account.getId(), type, amount);
            if (! operationInserted) {
                log.debug("Operation on account {} was not applied", account.getId());
                return Response.notModified("Operation was not applied").build();
            }
            BigDecimal newBalance = type == OperationType.WITHDRAWAL ?
                    account.getBalance().subtract(amount) :
                    account.getBalance().add(amount);
            boolean balanceUpdated = dao.updateBalance(handle, account.getId(), newBalance);
            if (balanceUpdated) {
                log.debug("Operation successfully applied");
                return Response.ok().build();
            } else {
                log.debug("Operation on account processed but account was not updated, rolling transaction back");
                dao.rollbackActions(handle);
                return Response.notModified().entity("Balance was not updated").build();
            }
        });
    }

    /**
     * Fetch account
     *
     * @param number account number
     * @return account
     */
    private InternalAccount fetchAccount(String number) {
        Optional<InternalAccount> accountOpt = dao.findAccount(number);
        if (! accountOpt.isPresent()) {
            log.debug("Account '{}' was not found", number);
            throw new NotFoundException("Account was not found");
        }
        return accountOpt.get();
    }

}
