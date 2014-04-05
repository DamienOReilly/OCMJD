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

    /////////////////////////////////////////////////////////////////////////////////////////////
    // READ
    /////////////////////////////////////////////////////////////////////////////////////////////

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

    /////////////////////////////////////////////////////////////////////////////////////////////
    // LOCK
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testLockRecord() throws RecordNotFoundException {
        long cookie = data.lock(0);
        assertThat(cookie, is(not(0L)));
    }

    @Test
    public void testLockUnlockLockAgainRecord() throws RecordNotFoundException, SecurityException {
        long cookie1 = data.lock(0);
        assertThat(cookie1, is(not(0L)));
        data.unlock(0, cookie1);
        long cookie2 = data.lock(0);
        assertThat(cookie2, is(not(0L)));
        assertThat(cookie2, not(equalTo(cookie1)));
    }

    @Test(expected = SecurityException.class)
    public void testUnlockNotLocked() throws SecurityException, RecordNotFoundException {
        data.unlock(0, 1234L);
    }

    @Test(expected = RecordNotFoundException.class)
    public void testLockRecordNotFound() throws SecurityException, RecordNotFoundException {
        data.unlock(7000, 1234L);

    }

    @Test(expected = RecordNotFoundException.class)
    public void testUnLockRecordNotFound() throws SecurityException, RecordNotFoundException {
        data.unlock(7000, 1234L);

    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    // DELETE
    /////////////////////////////////////////////////////////////////////////////////////////////


    @Test(expected = SecurityException.class)
    public void testDeleteNotLocked() throws RecordNotFoundException, SecurityException {
        data.delete(0, 0);
    }

    @Test(expected = SecurityException.class)
    public void testDeleteWithWrongCookie() throws RecordNotFoundException, SecurityException {
        long cookie = data.lock(0);
        data.delete(0, 0);
        data.unlock(0, 1234L);
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteTest1() throws RecordNotFoundException, SecurityException {
        long cookie = data.lock(0);
        data.delete(0, cookie);
        data.unlock(0, cookie);
        String[] record = data.read(0);
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteTest2() throws RecordNotFoundException, SecurityException {
        long cookie = data.lock(5);
        data.delete(5, cookie);
        data.unlock(5, cookie);

        data.close();
        data = new Data(COPY_DATABASE);

        String[] record = data.read(5);
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
