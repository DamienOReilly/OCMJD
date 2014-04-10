package suncertify.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
        data.read(-675);
    }

    @Test(expected = RecordNotFoundException.class)
    public void readNonExistingRecordTest() throws RecordNotFoundException {
        data.read(28);
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
        data.lock(0);
        data.delete(0, 0);
        data.unlock(0, 1234L);
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteTest1() throws RecordNotFoundException, SecurityException {
        long cookie = data.lock(0);
        data.delete(0, cookie);
        data.unlock(0, cookie);
        data.read(0);
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteTest2() throws RecordNotFoundException, SecurityException {
        long cookie = data.lock(5);
        data.delete(5, cookie);
        data.unlock(5, cookie);

        // re-initialize
        data.close();
        data = new Data(COPY_DATABASE);
        data.read(5);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    // CREATE
    /////////////////////////////////////////////////////////////////////////////////////////////


    @Test
    public void createRecord() throws DuplicateKeyException, RecordNotFoundException {
        String[] record = {"Damo", "Athlone", "Heating, Painting, Plumbing", "1", "$999.50", ""};
        data.create(record);
        String[] newRecord = data.read(28);
        assertArrayEquals(record, newRecord);
    }


    @Test(expected = DuplicateKeyException.class)
    public void createDuplicateRecord() throws DuplicateKeyException, RecordNotFoundException {
        String[] record = {"Damo", "Athlone", "Heating, Painting, Plumbing", "1", "$999.50", ""};
        data.create(record);
        data.create(record);
    }

    @Test
    public void createRecordRelpacingDeleted() throws DuplicateKeyException, RecordNotFoundException, SecurityException {
        String[] record = {"Damo", "Athlone", "Heating, Painting, Plumbing", "1", "$999.50", ""};
        long cookie = data.lock(5);
        data.delete(5, cookie);
        data.unlock(5, cookie);
        int newRecId = data.create(record);
        String[] newRecord = data.read(5);
        assertEquals(5, newRecId);
        assertArrayEquals(record, newRecord);
    }

    // Deleted record is not unlocked yet, so a new created record cannot use that slot.
    @Test(expected = RecordNotFoundException.class)
    public void createRecordReplacingLockedDeleted() throws DuplicateKeyException, RecordNotFoundException, SecurityException {
        String[] record = {"Damo", "Athlone", "Heating, Painting, Plumbing", "1", "$999.50", ""};
        long cookie = data.lock(5);
        data.delete(5, cookie);

        int newRecId = data.create(record);
        String[] newRecord = data.read(5);
        assertEquals(5, newRecId);
        assertArrayEquals(record, newRecord);
    }

    @Test
    public void createRecordReplacingLockedDeletedUnlocked() throws DuplicateKeyException, RecordNotFoundException, SecurityException {
        String[] record1 = {"Damo", "Athlone", "Heating, Painting, Plumbing", "1", "$999.50", ""};
        String[] record2 = {"Kody", "Athlone", "Heating, Painting, Plumbing", "1", "$999.50", ""};
        long cookie = data.lock(5);
        data.delete(5, cookie);

        int newRecId1 = data.create(record1);
        data.unlock(5, cookie);
        int newRecId2 = data.create(record2);

        assertThat(5, is(not(equalTo(newRecId1))));
        assertEquals(5, newRecId2);
        String[] newRecord2 = data.read(5);
        assertArrayEquals(record2, newRecord2);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATE
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void updateRecord() throws RecordNotFoundException, SecurityException {
        String[] record = {"Damo", "Athlone", "Heating, Painting, Plumbing", "1", "$999.50", ""};
        long cookie = data.lock(5);
        data.update(5, record, cookie);
        data.unlock(5, cookie);

        String[] newEdit = data.read(5);
        assertArrayEquals(record, newEdit);
    }

    @Test(expected = SecurityException.class)
    public void updateLockedRecord() throws RecordNotFoundException, SecurityException {
        String[] record = {"Damo", "Athlone", "Heating, Painting, Plumbing", "1", "$999.50", ""};
        data.lock(5);
        data.update(5, record, 1234L);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    // FIND
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void findRecord() {
        String[] criteria1 = {"Dogs With Tools", "Smallville", "Roofing", "7", "$35.00", null};
        int[] found1 = data.find(criteria1);
        assertArrayEquals(new int[]{0}, found1);

        String[] criteria2 = {"Dogs With Tools", null, null, null, null, null};
        int[] found2 = data.find(criteria2);
        assertArrayEquals(new int[]{0, 4, 5, 8, 16, 25}, found2);

        String[] criteria3 = {null, "Smallville", null, null, null, null};
        int[] found3 = data.find(criteria3);
        assertArrayEquals(new int[]{0, 1}, found3);
    }

    @Test
    public void findNewRecords() throws DuplicateKeyException {
        String[] record1 = {"Damo", "Arizona", "Heating, Painting, Plumbing", "1", "$999.50", ""};
        String[] record2 = {"Kody", "Arizona", "Heating, Painting, Plumbing", "1", "$999.50", ""};

        data.create(record1);
        data.create(record2);

        String[] criteria = {null, "Arizona", null, null, null, null};
        int[] found = data.find(criteria);
        assertArrayEquals(new int[]{28, 29}, found);
    }

    @Test
    public void findNewRecordsAfterDelete() throws DuplicateKeyException, RecordNotFoundException, SecurityException {
        String[] record1 = {"Damo", "Arizona", "Heating, Painting, Plumbing", "1", "$999.50", ""};
        String[] record2 = {"Kody", "Arizona", "Heating, Painting, Plumbing", "1", "$999.50", ""};

        data.create(record1);
        long cookie = data.lock(5);
        data.delete(5, cookie);
        data.unlock(5, cookie);
        data.create(record2);

        String[] criteria = {null, "Arizona", null, null, null, null};
        int[] found = data.find(criteria);
        assertArrayEquals(new int[]{5, 28}, found);
    }


    @Test
    public void findRecordsAfterUpdate() throws DuplicateKeyException, RecordNotFoundException, SecurityException {
        String[] record1 = {"Damo", "Arizona", "Drinking", "1", "$999.50", ""};
        String[] record2 = {"Kody", "Arizona", "Drinking", "1", "$999.50", ""};

        long cookie1 = data.lock(6);
        long cookie2 = data.lock(14);

        data.update(6, record1, cookie1);
        data.update(14, record2, cookie2);

        data.unlock(6, cookie1);
        data.unlock(14, cookie2);

        String[] criteria = {null, null, "Drinking", null, null, null};
        int[] found = data.find(criteria);
        assertArrayEquals(new int[]{6, 14}, found);
    }

    @Test
    public void findAll() {
        String[] criteria = {null, null, null, null, null, null};
        int[] found = data.find(criteria);
        assertEquals(found.length, 28);
    }

    @After
    public void tearDown() {
        data.close();
        DatabaseSchema.OFFSET_START_OF_RECORDS = 0;
        DatabaseSchema.NUMBER_OF_FIELDS = 0;
        DatabaseSchema.RECORD_SIZE_IN_BYTES = DatabaseSchema.DELETE_RECORD_FLAG_SIZE_IN_BYTES;
        try {
            Files.deleteIfExists(Paths.get(COPY_DATABASE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
