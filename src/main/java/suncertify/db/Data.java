package suncertify.db;

/**
 * This class implements {@link suncertify.db.DB} and provides methods to read, update and delete records in the
 * database.
 *
 * @author Damien O'Reilly
 */
public class Data implements DB {

    /**
     * Class that deals with low level access to the database. This will keep this class simplified by delegating all
     * work to the DatabaseIO class.
     */
    private final DatabaseIO database;

    /**
     * Lock Handler for transactional safety.
     */
    private final DatabaseLockHandler databaseLockHandler;

    /**
     * Constructor that takes in a String location to the database file on disk.
     *
     * @param databasePath Path to the database file on disk.
     */
    public Data(String databasePath) {
        databaseLockHandler = new DatabaseLockHandler();
        database = new DatabaseIO(databasePath, databaseLockHandler);
        setupShutdownHook();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] read(int recNo) throws RecordNotFoundException {
        return database.read(recNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(int recNo, String[] data, long lockCookie) throws RecordNotFoundException, SecurityException {
        database.update(recNo, data, lockCookie);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(int recNo, long lockCookie) throws RecordNotFoundException, SecurityException {
        database.delete(recNo, lockCookie);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] find(String[] criteria) {
        return database.find(criteria);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int create(String[] data) throws DuplicateKeyException {
        return database.create(data);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long lock(int recNo) throws RecordNotFoundException {
        database.checkRecordIsValid(recNo);
        return databaseLockHandler.lock(recNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlock(int recNo, long cookie) throws RecordNotFoundException, SecurityException {
        if (database.recordIDExists(recNo)) {
            databaseLockHandler.unlock(recNo, cookie);
        } else {
            throw new RecordNotFoundException("Record " + recNo + " does not exist.");
        }
    }

    /**
     * Close the file handler to the database. Public to assist Unit tests.
     */
    void close() {
        database.close();
    }

    /**
     * Setup a hook to cleanly close the database file handler before the VM terminates.
     */
    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                close();
            }
        });
    }
}
