package suncertify.db;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Damien O'Reilly
 */
public class DataTest {

    @Test
    public void readFirstRecordTest() {
        Data data = new Data("db-2x2.db");
        String[] first = {"Dogs With Tools", "Smallville", "Roofing", "7", "$35.00", ""};
        try {
            String[] firstRecord = data.read(0);
            assertArrayEquals(first, firstRecord);
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readLastRecordTest() {
        Data data = new Data("db-2x2.db");
        String[] last = {"Swanders & Flaughn", "Lendmarch", "Heating, Painting, Plumbing", "8", "$85.00", ""};
        try {
            String[] lastRecord = data.read(27);
            assertArrayEquals(last, lastRecord);
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = RecordNotFoundException.class)
    public void readInvalidRecordTest() throws RecordNotFoundException {
        Data data = new Data("db-2x2.db");
        String[] record = data.read(-675);
    }

    @Test(expected = RecordNotFoundException.class)
    public void readNonExistingRecordTest() throws RecordNotFoundException {
        Data data = new Data("db-2x2.db");
        String[] record = data.read(28);
    }


}
