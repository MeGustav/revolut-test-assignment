package com.megustav.revolut.misc.impl;

import com.google.inject.Singleton;
import com.megustav.revolut.misc.BlockingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of blocking service
 *
 * Service should use some sort of invalidation (or maybe {@link java.util.WeakHashMap})
 * But for the sake of the assignment won't consider it.
 *
 * @author MeGustav
 * 21/05/2018 22:38
 */
@Singleton
public class BlockingServiceImpl implements BlockingService {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(BlockingServiceImpl.class);

    /** Locks */
    private final Map<String, Lock> locks = new ConcurrentHashMap<>();

    @Override
    public Lock getLock(String name) {
        log.trace("Getting lock for: {}", name);
        Lock lock = locks.containsKey(name) ?
                locks.get(name) :
                createLock(name);
        log.trace("Got lock for {}", name);
        return lock;
    }

    /**
     * Create lock.
     * Performs double check locking
     *
     * @param name lock name
     * @return lock
     */
    private synchronized Lock createLock(String name) {
        if (! locks.containsKey(name)) {
            log.trace("Creating lock for {}", name);
            locks.put(name, new ReentrantLock());
        }
        return locks.get(name);
    }

}
