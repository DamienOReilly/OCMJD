package suncertify.db.io;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Damien O'Reilly
 */
public class DataBaseIOTest {

    private static RandomAccessFile database;
    private static DataBaseIO dataBaseIO;

    @BeforeClass
    public static void setupClass() {
        try {
            database = new RandomAccessFile("db-2x2.db", "r");
            dataBaseIO = new DataBaseIO(database);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                database.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    /**
     * Reset offset to zero before each test.
     */
    @Before
    public void setUp() {
        try {
            database.seek(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isCompatibleTest() {
        boolean isCompatible = dataBaseIO.isCompatible();
        assertTrue(isCompatible);
    }

    @Test
    public void loadRecordsTest() {
        dataBaseIO.isCompatible();
        dataBaseIO.parseDatabaseMetaData();
        List<String[]> records = dataBaseIO.loadRecords();
        // original db has 28 records.
        assertEquals(28, records.size());
        // just verify first and last record are correct
        String[] first = {"Dogs With Tools", "Smallville", "Roofing", "7", "$35.00", ""};
        String[] last = {"Swanders & Flaughn", "Lendmarch", "Heating, Painting, Plumbing", "8", "$85.00", ""};
        assertArrayEquals(first, records.get(0));
        assertArrayEquals(last, records.get(27));
    }

    @AfterClass
    public static void tearDownClass() {
        try {
            database.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
