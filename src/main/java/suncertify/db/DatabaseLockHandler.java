package suncertify.db;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Damien O'Reilly
 */
public class DatabaseLockHandler {

    /**
     * Singleton instance for the transactional safety lock manager.
     */
    private static final DatabaseLockHandler databaseLockHandler = new DatabaseLockHandler();

    /**
     * Hash Map containing records and their locks.
     */
    private final Map<Integer, Long> locks = new HashMap<>();

    /**
     * Logger instance.
     */
    private Logger logger = Logger.getLogger("suncertify.db");

    /**
     * Private constructor - Singleton pattern.
     */
    private DatabaseLockHandler() {
    }

    public static DatabaseLockHandler getInstance() {
        return databaseLockHandler;
    }

    /**
     * Locks a record
     *
     * @param recNo
     *         The record to attempt to lock.
     * @return A unique cookie that must be used when further interacting with this record.
     */
    public synchronized long lock(int recNo) throws RecordNotFoundException {
        long lockCookie = System.nanoTime();

        while (isLocked(recNo)) {
            try {
                /**
                 * If the specified record is already locked by a different client, the current thread gives up the
                 * CPU and consumes no CPU cycles until the record is unlocked.
                 */
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace(); //TODO: handle exception.
                throw new RecordNotFoundException("Problem when locking record: " + recNo + ". " + e.getMessage());
            }
        }

        locks.put(recNo, lockCookie);
        return lockCookie;
    }

    /**
     * Unlocks a locked record.
     *
     * @param recNo
     *         Record number to unlock.
     * @param cookie
     *         Cookie that the record was previously locked with.
     * @throws SecurityException
     *         Incorrect cookie or record is not currently locked.
     */
    public synchronized void unlock(int recNo, long cookie) throws SecurityException {
        // Check record is locked first.
        if (!isLocked(recNo)) {
            throw new SecurityException("Record is not currently locked.");
        }

        // Verify input cookie matches cookie of locked record.
        if (checkCookie(recNo, cookie)) {
            locks.remove(recNo);
            notifyAll();
        } else {
            throw new SecurityException("Provided cookie does not match cookie of locked record.");
        }
    }

    /**
     * Checks if the specified cookie matches that of the locked record.
     *
     * @param recNo
     *         The record number to check against.
     * @param cookie
     *         The cookie to check against.
     * @return If the cookies match or not.
     */
    private boolean checkCookie(int recNo, long cookie) {
        return locks.get(recNo) == cookie;
    }

    /**
     * Checks if a particular record is locked.
     *
     * @param recNo
     *         Record number to check if locked.
     * @return True if locked.
     */
    public boolean isLocked(int recNo) {
        return locks.containsKey(recNo);
    }
}
