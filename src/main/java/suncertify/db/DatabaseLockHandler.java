package suncertify.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * This class provides a locking mechanism in order to ensure transactional safety per record in the database.
 *
 * @author Damien O'Reilly
 */
class DatabaseLockHandler {

    /**
     * Hash Map containing records and their locks.
     */
    private final Map<Integer, Long> locks = new HashMap<>();

    /**
     * Logger instance.
     */
    private Logger logger = Logger.getLogger("suncertify.db");

    /**
     * Provides synchronized access to the mapLock cookie map.
     */
    private static final Lock mapLock = new ReentrantLock();

    /**
     * Provides a wait and notify thread mechanism.
     */
    private static final Condition lockCondition = mapLock.newCondition();

    /**
     * Locks a record
     *
     * @param recNo The record to attempt to mapLock.
     * @return A unique cookie that must be used when further interacting with this record.
     */
    public long lock(int recNo) {
        mapLock.lock();
        try {
            while (isLocked(recNo)) {
                /**
                 * If the specified record is already locked by a different client, the current thread gives up the
                 * CPU and consumes no CPU cycles until the record is unlocked.
                 */
                lockCondition.awaitUninterruptibly();
            }
            long lockCookie = System.nanoTime();
            locks.put(recNo, lockCookie);
            return lockCookie;
        } finally {
            mapLock.unlock();
        }
    }

    /**
     * Unlocks a locked record. Notifies any waiting threads (if any) on this record that it is now unlocked. If a
     * record is already deleted, its locked state is cleared. This is a valid scenario.
     *
     * @param recNo  Record number to unlock.
     * @param cookie Cookie that the record was previously locked with.
     * @throws SecurityException Incorrect cookie or record is not currently locked.
     */
    public void unlock(int recNo, long cookie) throws SecurityException {
        mapLock.lock();
        try {
            // Check record is locked first.
            if (!isLocked(recNo)) {
                throw new SecurityException("Record is not currently locked.");
            }

            // Verify input cookie matches cookie of locked record.
            if (validateCookie(recNo, cookie)) {
                locks.remove(recNo);
                lockCondition.signalAll();
            } else {
                throw new SecurityException("Provided cookie does not match cookie of locked record.");
            }
        } finally {
            mapLock.unlock();
        }
    }

    /**
     * Checks if the specified cookie matches that of the locked record.
     *
     * @param recNo  The record number to check against.
     * @param cookie The cookie to check against.
     * @return If the cookies match or not.
     */
    private boolean validateCookie(int recNo, long cookie) {
        return locks.get(recNo) == cookie;
    }

    /**
     * A thread-safe method to check if a given cookie is valid for a given record.
     *
     * @param recNo  The record number to check against.
     * @param cookie If the cookies match or not.
     * @return True if cookie is valid, otherwise false.
     */
    public boolean isCookieValid(int recNo, long cookie) {
        mapLock.lock();
        try {
            return validateCookie(recNo, cookie);
        } finally {
            mapLock.unlock();
        }
    }

    /**
     * Checks if a particular record is locked.
     *
     * @param recNo Record number to check if locked.
     * @return True if locked.
     */
    private boolean isLocked(int recNo) {
        return locks.containsKey(recNo);
    }

    /**
     * A thread-safe method to check if a particular record is locked.
     *
     * @param recNo Record number to check if locked.
     * @return True if locked.
     */
    public boolean isRecordUnlocked(int recNo) {
        mapLock.lock();
        try {
            return !isLocked(recNo);
        } finally {
            mapLock.unlock();
        }
    }
}