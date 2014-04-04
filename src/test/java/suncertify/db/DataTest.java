package suncertify.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Damien O'Reilly
 */
public class DataTest {

    private Data data = null;
    private static final String ORIGINAL_DATABASE = "db-2x2.db";
    private static final String COPY_DATABASE = "db-2x2.db-test";

    @Before
    public void setUp() {
        try {
            Files.copy(Paths.get(ORIGINAL_DATABASE), Paths.get(COPY_DATABASE), StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = new Data(COPY_DATABASE);
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
            assertThat(cookie2, is(not(0L)));
            assertThat(cookie2, not(equalTo(cookie1)));
            //data.unlock(0, cookie2);
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Test(expected = SecurityException.class)
    public void testDeleteWithoutLocking() throws RecordNotFoundException {
        //data.delete(0, 0);
    }

    @Test
    public void testDeleteWithWrongCookie() throws RecordNotFoundException {
        long cookie1 = data.lock(0);
        data.delete(0, 0);
        data.unlock(0, 1234L);
    }

    @After
    public void tearDown() {
        data.close();
        try {
            Files.deleteIfExists(Paths.get(COPY_DATABASE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
