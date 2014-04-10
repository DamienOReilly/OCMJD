package suncertify.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Utility class to perform common operations to the project.
 *
 * @author Damien O'Reilly
 */
public class Utils {
    /**
     * Reads a string from bytes accessed via {@link java.io.RandomAccessFile}
     *
     * @param database Database
     * @param length   Length of String to read
     * @return String extracted.
     * @throws IOException
     */
    public static String readBytesAsString(RandomAccessFile database, int length, String encoding) throws IOException {
        byte[] input = new byte[length];
        database.read(input);
        return new String(input, encoding).trim();
    }

    /**
     * Converts an {@link java.util.ArrayList} of {@link java.lang.Integer} to a primitive int array.
     *
     * @param integers {@link java.util.ArrayList} of {@link java.lang.Integer}
     * @return An int array.
     */
    public static int[] integerArrayListToPrimitiveArray(ArrayList<Integer> integers) {
        int[] ints = new int[integers.size()];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = integers.get(i).intValue();
        }
        return ints;
    }
}
