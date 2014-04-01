package suncertify.utils;

import suncertify.db.DatabaseSchema;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Damien O'Reilly
 */
public class Utils {
    /**
     * Reads a string from bytes accessed via {@link java.io.RandomAccessFile}
     *
     * @param database
     *         Database
     * @param length
     *         Length of String to read
     * @return String extracted.
     * @throws IOException
     */
    public static String readBytesAsString(RandomAccessFile database, int length) throws IOException {
        byte[] input = new byte[length];
        database.read(input);
        return new String(input, DatabaseSchema.CHARSET_ENCODING).trim();
    }
}
