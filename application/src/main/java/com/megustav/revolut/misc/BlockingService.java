package com.megustav.revolut.misc;

import java.util.concurrent.locks.Lock;

/**
 * Blocking service contract
 *
 * @author MeGustav
 * 21/05/2018 22:38
 */
public interface BlockingService {

    /**
     * Get the lock associated with the name
     *
     * @param name key
     * @return lock
     */
    Lock getLock(String name);

}
