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
//        checkRecordId(recNo);
//        return records.get(recNo);
        return null;
    }

    @Override
    public void update(int recNo, String[] data, long lockCookie) throws RecordNotFoundException, SecurityException {

    }

    @Override
    public void delete(int recNo, long lockCookie) throws RecordNotFoundException, SecurityException {

    }

    @Override
    public int[] find(String[] criteria) {
        return new int[0];
    }

    @Override
    public int create(String[] data) throws DuplicateKeyException {
        return 0;
    }

    @Override
    public long lock(int recNo) throws RecordNotFoundException {
        //checkRecordId(recNo);
        return 0;
    }

    @Override
    public void unlock(int recNo, long cookie) throws RecordNotFoundException, SecurityException {

    }


}
