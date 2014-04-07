package suncertify.db;

import java.util.LinkedList;
import java.util.List;

/**
 * This class deals with database schema specific details.
 *
 * @author Damien O'Reilly
 */
public class DatabaseSchema {

    /**
     * Magic to identify a compatible database with this application.
     */
    public static final int DATABASE_COMPATIBILITY_MAGIC = 0x202;

    /**
     * Identifies a deleted record.
     */
    public static final int DELETED_RECORD_FLAG = 0x8000;

    /**
     * Identifies a valid record.
     */
    public static final int VALID_RECORD_FLAG = 0x0;

    /**
     * Deleted record flag size in bytes.
     */
    public static final int DELETE_RECORD_FLAG_SIZE_IN_BYTES = 2;

    /**
     * Padding for fields in a record where input is shorter than field length.
     */
    public static final byte FIELD_PADDING = 0x20;

    /**
     * Character set for field contents, as per project requirements.
     */
    public static final String CHARSET_ENCODING = "US-ASCII";

    /**
     * LinkedList to preserve order
     */
    public static List<Field> fields = new LinkedList<>();

    /**
     * Offset where records begin in the database.
     */
    public static int OFFSET_START_OF_RECORDS;

    /**
     * Number of fields per record.
     */
    public static short NUMBER_OF_FIELDS;

    /**
     * The delete flag is part of the overall record size.
     */
    public static int RECORD_SIZE_IN_BYTES = DELETE_RECORD_FLAG_SIZE_IN_BYTES;

}
