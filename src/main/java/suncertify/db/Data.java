package suncertify.db;

import java.io.FileNotFoundException;

/**
 * @author Damien O'Reilly
 */
public class Data implements DB {

    /**
     * Class that deals with low level access to the database. This keeps this class simplified.
     */
    private static DatabaseIO database;

    /**
     * Lock Handler for transactional safety.
     */
    private DatabaseLockHandler databaseLockHandler;

    /**
     * @param databasePath Path to the database file on disk.
     */
    public Data(String databasePath) {
        databaseLockHandler = new DatabaseLockHandler();
        try {
            database = new DatabaseIO(databasePath, databaseLockHandler);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String[] read(int recNo) throws RecordNotFoundException {
        return database.read(recNo);
    }

    @Override
    public void update(int recNo, String[] data, long lockCookie) throws RecordNotFoundException {

    }

    @Override
    public void delete(int recNo, long lockCookie) throws RecordNotFoundException, SecurityException {
        database.delete(recNo, lockCookie);

    }

    @Override
    public int[] find(String[] criteria) {
        return new int[0];
    }

    /**
     * @param data The record details
     * @return
     * @throws DuplicateKeyException
     */
    @Override
    public int create(String[] data) throws DuplicateKeyException {
        database.create(data);
        return 0;
    }

    /**
     * @param recNo The record number of the record to lock
     * @return
     * @throws RecordNotFoundException
     */
    @Override
    public long lock(int recNo) throws RecordNotFoundException {
        database.checkRecordIsValid(recNo);
        return databaseLockHandler.lock(recNo);
    }

    /**
     * @param recNo  The record number of the record to unlock. Note, the record may no longer exist if it was
     *               deleted post locking
     * @param cookie The cookie that the record was originally locked with
     * @throws RecordNotFoundException
     * @throws SecurityException
     */
    @Override
    public void unlock(int recNo, long cookie) throws RecordNotFoundException, SecurityException {
        if (database.recordIDExists(recNo)) {
            databaseLockHandler.unlock(recNo, cookie);
        } else {
            throw new RecordNotFoundException("Record " + recNo + " does not exist.");
        }
    }

    public void close() {
        database.close();
    }
}
