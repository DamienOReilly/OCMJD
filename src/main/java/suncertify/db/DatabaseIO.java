package suncertify.db;

import suncertify.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * This class provides low level functions for interacting with the database. This class is a worker class for
 * {@link suncertify.db.Data}. It should be possible to modify this class without effecting end user perspective.
 *
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
                    database.seek(database.getFilePointer() + DatabaseSchema.RECORD_SIZE_IN_BYTES -
                            DatabaseSchema.DELETE_RECORD_FLAG_SIZE_IN_BYTES);
                }
                //Add the record to cache. If record was deleted, an empty String array is added as a place-holder.
                records.add(record);
            }
        } catch (IOException e) {
            //TODO: log
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
            //TODO: log
            e.printStackTrace();
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
            //TODO: log
            e.printStackTrace();
        }

    }

    /**
     * Returns a list of records loaded from the database.
     *
     * @return All records as a {@link java.util.List} of type {@link String}.
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
     * Validates data submitted to create a new record.
     *
     * @param data {@link String} array containing elements for new record.
     * @throws DuplicateKeyException If a record containing submitted name and address already exists.
     */
    private void validateNewRecord(String[] data) throws DuplicateKeyException {
        if ((data == null) || (data[0] == null) || (data[1] == null) || data[0].equals("") || data[1].equals("")) {
            throw new IllegalArgumentException("Invalid record. A record must contain at least a name and address.");
        }

        // Check is record already exists. Based on name and address.
        for (int i = 0; i < records.size(); i++) {
            if (!isRecordDeleted(i)) {
                String[] record = records.get(i);
                if (data[0].equalsIgnoreCase(record[0]) && data[1].equalsIgnoreCase(record[1])) {
                    throw new DuplicateKeyException("A record with that name and address already exists.");
                }
            }
        }
    }

    /**
     * Closes the database.
     */
    public void close() {
        try {
            database.close();
        } catch (IOException e) {
            //TODO: log
            e.printStackTrace();
        }
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

    /**
     * Checks a record's lock status against the given cookie.
     *
     * @param recNo      Record ID.
     * @param lockCookie Lock cookie.
     * @throws SecurityException If the record isn't locked or is locked with a different cookie than the supplied
     *                           cookie.
     */
    private void verifyLockStatus(int recNo, long lockCookie) throws SecurityException {
        if (!databaseLockHandler.isRecordLocked(recNo)) {
            throw new SecurityException("Record " + recNo + " is not locked. Cannot delete.");
        }
        if (!databaseLockHandler.isCookieValid(recNo, lockCookie)) {
            throw new SecurityException("Wrong lock cookie for record " + recNo + ". Cannot delete.");
        }
    }

    /**
     * Gets a record's offset in the database.
     *
     * @param recNo Record ID.
     * @return Offset for given Record ID.
     */
    private int getRecordOffset(int recNo) {
        return DatabaseSchema.OFFSET_START_OF_RECORDS + (recNo * DatabaseSchema.RECORD_SIZE_IN_BYTES);
    }

    /**
     * Gets a Record ID from a given record offset in the database.
     *
     * @param position Offset of start of record in the database.
     * @return Record ID.
     */
    private int getRecordIdFromOffset(long position) {
        return (int) ((position - DatabaseSchema.OFFSET_START_OF_RECORDS)
                / DatabaseSchema.RECORD_SIZE_IN_BYTES);
    }

    /**
     * Method to determine where to insert a new record. Be it a previously deleted record or insert a new row at
     * end of database.
     * <p/>
     * This method will check if record is flagged as deleted and is not currently locked.
     * It is possible for a record to be flagged as deleted while its record id can still remain locked. This can
     * happen if a client locks a record, deletes it, but hasn't called unlock() yet.
     *
     * @return Offset to write to.
     * @throws IOException if there was a problem writing to the database.
     */
    private long getOffsetForNewRecord() throws IOException {
        long currentPosition;
        database.seek(DatabaseSchema.OFFSET_START_OF_RECORDS);
        while (database.getFilePointer() < database.length()) {
            currentPosition = database.getFilePointer();
            int flag = database.readUnsignedShort();

            if (flag == DatabaseSchema.DELETED_RECORD_FLAG &&
                    !databaseLockHandler.isRecordLocked(getRecordIdFromOffset(currentPosition))) {
                return currentPosition;
            }

            // Move onto the next record offset. Remember, the deleted flag is 2 bytes
            database.seek(database.getFilePointer() + DatabaseSchema.RECORD_SIZE_IN_BYTES -
                    DatabaseSchema.DELETE_RECORD_FLAG_SIZE_IN_BYTES);
        }

        return database.getFilePointer();
    }

    /**
     * Writes data to the database file.
     *
     * @param data     The record to write.
     * @param position Position in the database file to write the record.
     * @throws IOException Problem writing to the database.
     */
    private void write(String[] data, long position) throws IOException {
        database.seek(position);
        database.writeShort(DatabaseSchema.VALID_RECORD_FLAG);

        for (int i = 0; i < DatabaseSchema.NUMBER_OF_FIELDS; i++) {
            byte[] field = Arrays.copyOf(data[i].getBytes(DatabaseSchema.CHARSET_ENCODING),
                    DatabaseSchema.fields.get(i).getLength());
            Arrays.fill(field, data[i].length(), field.length, DatabaseSchema.FIELD_PADDING);
            database.write(field);
        }
    }

    /**
     * Reads a record from the database
     *
     * @param recNo Record ID of the record to read.
     * @return String array for the specific record.
     * @throws RecordNotFoundException If the Record ID does not exist or is flagged as deleted.
     */
    public String[] read(int recNo) throws RecordNotFoundException {
        checkRecordIsValid(recNo);
        return records.get(recNo);
    }

    /**
     * Creates a new record. Will use slot of a previously deleted record if one exists.
     *
     * @param data New record data.
     * @return Record ID assigned to the newly created record.
     * @throws DuplicateKeyException If an entry with name name and address already exists.
     */
    public int create(String[] data) throws DuplicateKeyException {
        try {
            dbWriteLock.lock();
            validateNewRecord(data);
            long position = getOffsetForNewRecord();
            if (position == 0) {
                //TODO: Cannot determine offset to insert new record.
            }

            write(data, position);

            // Check if existing entry in cache is to be updated, or new entry created.
            int recId = getRecordIdFromOffset(position);
            if (records.size() > recId) {
                records.set(recId, data);
                return recId;
            } else {
                records.add(data);
                return recId;
            }

        } catch (IOException e) {
            //TODO: log
            e.printStackTrace();
        } finally {
            dbWriteLock.unlock();
        }

        return Integer.MIN_VALUE;
    }

    /**
     * Deletes a record from the database.
     *
     * @param recNo      Record ID of the record to delete.
     * @param lockCookie Cookie that the record was previously locked with.
     * @throws RecordNotFoundException If the Record ID does not exist or is flagged as deleted.
     * @throws SecurityException       If the input cookie does not match cookie of locked record, or if record is not
     *                                 currently locked.
     */
    public void delete(int recNo, long lockCookie) throws RecordNotFoundException, SecurityException {
        dbWriteLock.lock();
        try {
            checkRecordIsValid(recNo);
            verifyLockStatus(recNo, lockCookie);

            database.seek(getRecordOffset(recNo));
            database.writeShort(DatabaseSchema.DELETED_RECORD_FLAG);
            // Reset the record in the cache. (Empty String array)
            records.set(recNo, new String[DatabaseSchema.NUMBER_OF_FIELDS]);

        } catch (IOException e) {
            //TODO: log
            e.printStackTrace();
            throw new RecordNotFoundException("Could not delete record " + recNo + ". " + e.getLocalizedMessage());
        } finally {
            dbWriteLock.unlock();
        }

    }

    /**
     * Updates a record in the database with the supplied record information.
     *
     * @param recNo      The Record ID of the record to update.
     * @param data       The record data to update the record with.
     * @param lockCookie Cookie that the record was previously locked with.
     * @throws RecordNotFoundException If the Record ID does not exist or is flagged as deleted.
     * @throws SecurityException       If the supplied cookie does not match the cookie this record is locked with or if
     *                                 the record is not locked in the first place.
     */
    public void update(int recNo, String[] data, long lockCookie) throws RecordNotFoundException, SecurityException {
        dbWriteLock.lock();
        try {
            checkRecordIsValid(recNo);
            verifyLockStatus(recNo, lockCookie);
            int position = getRecordOffset(recNo);
            write(data, position);
            records.set(recNo, data);
        } catch (IOException e) {
            //TODO: log
            e.printStackTrace();
        } finally {
            dbWriteLock.unlock();
        }

    }
}
