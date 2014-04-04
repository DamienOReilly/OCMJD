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
     * Singleton Lock Handler for transactional safety.
     */
    private static DatabaseLockHandler databaseLockHandler = DatabaseLockHandler.getInstance();

    /**
     * @param databasePath
     *         Path to the database file on disk.
     */
    public Data(String databasePath) {
        try {
            database = new DatabaseIO(databasePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String[] read(int recNo) throws RecordNotFoundException {
        return database.read(recNo);
    }

    @Override
    public void update(int recNo, String[] data, long lockCookie) throws RecordNotFoundException, SecurityException {

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
     * @param recNo
     *         The record number of the record to lock
     * @return
     * @throws RecordNotFoundException
     */
    @Override
    public long lock(int recNo) throws RecordNotFoundException {
        database.checkRecordId(recNo);
        return databaseLockHandler.lock(recNo);
    }

    /**
     *
     * @param recNo
     *         The record number of the record to unlock
     * @param cookie
     *         The cookie that the record was originally locked with
     * @throws RecordNotFoundException
     * @throws SecurityException
     */
    @Override
    public void unlock(int recNo, long cookie) throws RecordNotFoundException, SecurityException {
        database.checkRecordId(recNo);
        databaseLockHandler.unlock(recNo, cookie);
    }

    public void close() {
        database.close();
    }
}
