package suncertify.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Damien O'Reilly
 */
public class DatabaseIOTest {

    private DatabaseIO databaseIO;
    private DatabaseLockHandler databaseLockHandler;


    @Before
    public void setUp() {
        databaseLockHandler = new DatabaseLockHandler();
        try {
            databaseIO = new DatabaseIO("db-2x2.db", databaseLockHandler);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadRecordsTest() {
        List<String[]> records = databaseIO.getRecords();
        // original db has 28 records.
        assertEquals(28, records.size());
        // just verify first and last record are correct
        String[] first = {"Dogs With Tools", "Smallville", "Roofing", "7", "$35.00", ""};
        String[] last = {"Swanders & Flaughn", "Lendmarch", "Heating, Painting, Plumbing", "8", "$85.00", ""};
        assertArrayEquals(first, records.get(0));
        assertArrayEquals(last, records.get(27));
    }

    @After
    public void tearDown() {
        databaseIO.close();
    }

}