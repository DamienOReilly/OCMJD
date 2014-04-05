package suncertify.db;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Damien O'Reilly
 */
public class DatabaseSchema {

    // Magic to identify a compatible database with this application
    public static final int DATABASE_COMPATIBILITY_MAGIC = 0x202;

    public static final int DELETED_RECORD_FLAG = 0x8000;

    public static final int DELETE_RECORD_FLAG_SIZE_IN_BYTES = 2;

    public static int OFFSET_START_OF_RECORDS;

    public static short NUMBER_OF_FIELDS;

    // The delete flag is part of the overall record size.
    public static int RECORD_SIZE_IN_BYTES = DELETE_RECORD_FLAG_SIZE_IN_BYTES;

    /* LinkedList to preserve order */
    public static List<Field> fields = new LinkedList<>();

    /* Character set for field contents, as per project requirements. */
    public static final String CHARSET_ENCODING = "US-ASCII";
}
