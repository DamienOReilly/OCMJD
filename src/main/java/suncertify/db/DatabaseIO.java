package suncertify.db;

import suncertify.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author Damien O'Reilly
 */
public class DatabaseIO {

    /**
     * Database physical file.
     */
    private RandomAccessFile database;

    /**
     * List of records loaded from the database.
     */
    private List<String[]> records;

    /**
     * Lock Handler for transactional safety.
     */
    private DatabaseLockHandler databaseLockHandler;

    /**
     * Synchronized write access to the physical database file.
     */
    private Lock dbWriteLock = new ReentrantLock();

    /**
     * Constructor that take in database file location and parses the metadata and available records.
     *
     * @param databasePath Path to the database file.
     * @throws FileNotFoundException Database was not found.
     */
    public DatabaseIO(String databasePath, DatabaseLockHandler databaseLockHandler) throws FileNotFoundException {
        this.databaseLockHandler = databaseLockHandler;
        this.database = new RandomAccessFile(databasePath, "rw");
        if (isCompatible()) {
            parseDatabaseMetaData();
            records = loadRecords();
        }
    }

    /**
     * Reads and returns a list of records from the database as a {@link java.lang.String} array.
     * Ignores records flagged as 'deleted'.
     *
     * @return {@link java.lang.String} array containing records in the database.
     */
    private List<String[]> loadRecords() {

        List<String[]> records = null;

        try {
            database.seek(DatabaseSchema.OFFSET_START_OF_RECORDS);
            records = new ArrayList<>();

            while (database.getFilePointer() < database.length()) {
                int flag = database.readUnsignedShort();
                String[] record = new String[DatabaseSchema.NUMBER_OF_FIELDS];

                if (flag != DatabaseSchema.DELETED_RECORD_FLAG) {
                    for (int i = 0; i < DatabaseSchema.NUMBER_OF_FIELDS; i++) {
                        record[i] = Utils.readBytesAsString(database, DatabaseSchema.fields.get(i).getLength(),
                                DatabaseSchema.CHARSET_ENCODING);
                    }
                } else {
                    // Move onto the next record offset. Remember, the deleted flag is 2 bytes
                    database.seek(database.getFilePointer() + DatabaseSchema.RECORD_SIZE_IN_BYTES - 2);
                }
                //Add the record to cache. If record was deleted, an empty String array is added as a place-holder.
                records.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }

    /**
     * Method to ensure selected database is compatible with this application by checking its magic value.
     *
     * @return Returns true or false, depending on whether the specified database is compatible with this application
     * or not.
     */
    private boolean isCompatible() {
        int magic;
        try {
            magic = database.readInt();
            if (magic == DatabaseSchema.DATABASE_COMPATIBILITY_MAGIC) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: error reading db
        }
        return false;
    }

    /**
     * This method is responsible for parsing the meta-data from the database which includes information such as the
     * number of columns per record and their names and sizes.
     */
    private void parseDatabaseMetaData() {
        try {
            DatabaseSchema.OFFSET_START_OF_RECORDS = database.readInt();
            DatabaseSchema.NUMBER_OF_FIELDS = database.readShort();

            for (int i = 0; i < DatabaseSchema.NUMBER_OF_FIELDS; i++) {
                // Read the field name length.
                short fieldNameLength = database.readShort();
                // Read the actual field name.
                String fieldName = Utils.readBytesAsString(database, fieldNameLength, DatabaseSchema.CHARSET_ENCODING);
                // Read the associated column length for the above field.
                short fieldColumnLength = database.readShort();

                // Calculate record size. It will be used elsewhere.
                DatabaseSchema.RECORD_SIZE_IN_BYTES += fieldColumnLength;
                DatabaseSchema.fields.add(new Field(fieldName, fieldColumnLength));
            }

        } catch (IOException e) {
            e.printStackTrace();
            //TODO: error reading db
        }

    }

    /**
     * Returns a list of records loaded from the database.
     *
     * @return Records.
     */
    public List<String[]> getRecords() {
        return records;
    }

    /**
     * Method to determine if Record ID is valid and/or exists.
     *
     * @param recNo Record ID to verify
     * @throws RecordNotFoundException If the record was not found or is invalid.
     */
    public void checkRecordIsValid(int recNo) throws RecordNotFoundException {
        if (!recordIDExists(recNo)) {
            throw new RecordNotFoundException("Record ID " + recNo + " not found.");
        }
        if (isRecordDeleted(recNo)) {
            throw new RecordNotFoundException("Record ID " + recNo + " has been deleted.");
        }
    }

    /**
     * Checks if a Record ID exists, regardless if it is flagged as deleted.
     *
     * @param recNo Record ID
     * @return True if exists, otherwise false.
     */
    public boolean recordIDExists(int recNo) {
        try {
            records.get(recNo);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    /**
     * Method checks if a record is 'flagged' deleted, as opposed to Record ID not existing.
     *
     * @param recNo Record ID.
     * @return Returns true if deleted, false if not.
     */
    public boolean isRecordDeleted(int recNo) {
        return records.get(recNo)[0] == null;
    }

    public synchronized int create(String[] data) {
        return 0;
    }


    /**
     * Closes the database.
     */
    public void close() {
        try {
            database.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a record from the database
     *
     * @param recNo Record ID.
     * @return String array for the specific record.
     * @throws RecordNotFoundException If the record is not found or record ID is invalid.
     */
    public String[] read(int recNo) throws RecordNotFoundException {
        checkRecordIsValid(recNo);
        return records.get(recNo);
    }

    /**
     * Deletes a record from the database.
     *
     * @param recNo      Record ID.
     * @param lockCookie Cookie that the record was previously locked with.
     * @throws RecordNotFoundException If the record was not found or record ID is invalid.
     * @throws SecurityException       If the input cookie does not match cookie of locked record, or if record is not
     *                                 currentyl locked.
     */
    public void delete(int recNo, long lockCookie) throws RecordNotFoundException, SecurityException {
        dbWriteLock.lock();
        try {
            checkRecordIsValid(recNo);

            if (!databaseLockHandler.isLocked(recNo)) {
                throw new SecurityException("Record " + recNo + " is not locked. Cannot delete.");
            }
            if (!databaseLockHandler.validateCookie(recNo, lockCookie)) {
                throw new SecurityException("Wrong lock cookie for record " + recNo + ". Cannot delete.");
            }

            database.seek(getPosition(recNo));
            database.writeShort(DatabaseSchema.DELETED_RECORD_FLAG);
            // Reset the record in the cache. (Uninitialized String array)
            records.set(recNo, new String[DatabaseSchema.NUMBER_OF_FIELDS]);

        } catch (IOException e) {
            //TODO: log
            e.printStackTrace();
            throw new RecordNotFoundException("Could not delete record " + recNo + ". " + e.getLocalizedMessage());
        } finally {
            dbWriteLock.unlock();
        }

    }

    public int getPosition(int recNo) {
        return DatabaseSchema.OFFSET_START_OF_RECORDS + (recNo * DatabaseSchema.RECORD_SIZE_IN_BYTES);
    }

}
