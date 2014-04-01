package suncertify.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Damien O'Reilly
 */
public class DataTest {

    private Data data = null;

    @Before
    public void setUp() {
        data = new Data("db-2x2.db");
    }

    @Test
    public void readFirstRecordTest() {
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
        String[] record = data.read(-675);
    }

    @Test(expected = RecordNotFoundException.class)
    public void readNonExistingRecordTest() throws RecordNotFoundException {

        String[] record = data.read(28);
    }

    @Test
    public void testLockRecord() {
        try {
            long cookie = data.lock(0);
            assertThat(cookie, is(not(0L)));
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLockUnlockLockAgainRecord() {
        try {
            long cookie1 = data.lock(0);
            assertThat(cookie1, is(not(0L)));
            data.unlock(0, cookie1);
            long cookie2 = data.lock(0);
            assertThat(cookie1, is(not(0L)));
            assertThat(cookie1, not(equalTo(cookie2)));
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        data.close();
    }
}
