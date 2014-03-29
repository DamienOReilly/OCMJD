package suncertify.db;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * @author Damien O'Reilly
 */
public class Data implements DB {

    private String databasePath;
    private RandomAccessFile database;


    /**
     * @param databasePath
     *         Path to the database file on disk.
     */
    public Data(String databasePath) {
        this.databasePath = databasePath;
        loadDatabase();
    }

    /**
     *
     */
    private void loadDatabase() {
        try {
            database = new RandomAccessFile(databasePath, "rw");
        } catch (FileNotFoundException e) {
            //TODO: handle DB not found!
            e.printStackTrace();
        }
    }

    @Override
    public String[] read(int recNo) throws RecordNotFoundException {
        return new String[0];
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
        return 0;
    }

    @Override
    public void unlock(int recNo, long cookie) throws RecordNotFoundException, SecurityException {

    }
}
