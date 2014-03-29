package suncertify.db.io;

import suncertify.db.DatabaseSchema;
import suncertify.db.Field;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Damien O'Reilly
 */
public class DataBaseIO {
    private RandomAccessFile database;

    public DataBaseIO(RandomAccessFile database) {
        this.database = database;
    }

    /**
     * Reads and returns a list of records from the database as a {@link java.lang.String} array.
     * Ignores records flagged as 'deleted'.
     *
     * @return {@link java.lang.String} array containing records in the database.
     */
    public List<String[]> loadRecords() {

        List<String[]> records = null;

        try {
            database.seek(DatabaseSchema.OFFSET_START_OF_RECORDS);
            records = new ArrayList<>();

            while (database.getFilePointer() < database.length()) {
                short flag = database.readShort();
                if (flag != DatabaseSchema.DELETED_RECORD) {
                    String[] record = new String[DatabaseSchema.NUMBER_OF_FIELDS];

                    for (int i = 0; i < DatabaseSchema.NUMBER_OF_FIELDS; i++) {
                        record[i] = Utils.readBytesAsString(database, DatabaseSchema.fields.get(i).getLength());
                    }
                    records.add(record);
                } else {
                    /* Move onto the next record offset.
                    /* Remember, the deleted flag is 2 bytes */
                    database.seek(database.getFilePointer() + DatabaseSchema.RECORD_SIZE_IN_BYTES - 2);
                }
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
    public boolean isCompatible() {
        int magic = 0;
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
    public void parseDatabaseMetaData() {
        try {
            DatabaseSchema.OFFSET_START_OF_RECORDS = database.readInt();
            DatabaseSchema.NUMBER_OF_FIELDS = database.readShort();

            for (int i = 0; i < DatabaseSchema.NUMBER_OF_FIELDS; i++) {
                /* Read the field name length. */
                short fieldNameLength = database.readShort();
                /* Read the actual field name. */
                String fieldName = Utils.readBytesAsString(database, fieldNameLength);
                /* Read the associated column length for the above field. */
                short fieldColumnLength = database.readShort();

                /* Calculate record size. It will be used elsewhere. */
                DatabaseSchema.RECORD_SIZE_IN_BYTES += fieldColumnLength;
                DatabaseSchema.fields.add(new Field(fieldName, fieldColumnLength));
            }

        } catch (IOException e) {
            e.printStackTrace();
            //TODO: error reading db
        }

    }

    //TODO: remove, this is for test only.
    public static void main(String[] args) {
        DataBaseIO dataBaseIO;
        try {
            dataBaseIO = new DataBaseIO(new RandomAccessFile("db-2x2.db", "rw"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
