package suncertify.db;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Damien O'Reilly
 */
public class DatabaseLockHandler {

    /**
     * Singleton instance.
     */
    private static final DatabaseLockHandler databaseLockHandler = new DatabaseLockHandler();

    /**
     * Hash Map containing records and their locks
     */
    private final Map<Integer, Long> locks = new HashMap<>();

    /**
     * Private constructor - Singleton pattern.
     */
    private DatabaseLockHandler() {
    }

    public DatabaseLockHandler getInstance() {
        return databaseLockHandler;
    }

    /**
     * Locks a record
     *
     * @param recNo
     * @return
     */
    public synchronized long lock(int recNo) {
        long lockCookie = System.nanoTime();

        while (isLocked(recNo)) {
            try {
                /**
                 * If the specified record is already locked by a different client, the current thread gives up the
                 * CPU and consumes no CPU cycles until the record is unlocked.
                 */
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return 0;

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
