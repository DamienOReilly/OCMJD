package suncertify.db;
/**
 * This is a modification of Roberto's DataClassTest to be compatible with the Bodgitt & Scarper 2.1.2 assignment.
 *
 * @author Damien O'Reilly
 */

/*
 * @(#)DataClassTest.java    1.0 05/11/2008
 *
 * Candidate: Roberto Perillo
 * Prometric ID: Your Prometric ID here
 * Candidate ID: Your candidade ID here
 *
 * Sun Certified Developer for Java 2 Platform, Standard Edition Programming
 * Assignment (CX-310-252A)
 *
 * This class is part of the Programming Assignment of the Sun Certified
 * Developer for Java 2 Platform, Standard Edition certification program, must
 * not be used out of this context and must be used exclusively by Sun
 * Microsystems, Inc.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * The <code>DataClassTest</code> tests the main functionalities of the
 * {@link Data} class. In order to simulate several clients trying to use it and
 * exercise the locking mechanism, it also has several inner classes that extend
 * the {@link Thread} class, where each class represents one client requesting
 * one operation, and mainly requesting updating and deletion of records. The
 * <code>FindingRecordsThread</code> exercises two functionalities: finding
 * records and reading records.
 *
 * @author Roberto Perillo
 * @version 1.0 05/11/2008
 */
public class ConcurrencyTest {

    private static final String ORIGINAL_DATABASE = "db-2x2.db";
    private static final String COPY_DATABASE = "db-2x2.db-test";
    private static Data data = null;
    /*
     * If any preparation has to be done before using the Data class, it can be
     * done in a static block; in this case, before using the Data class, the
     * loadDbRecords method has to be called prior to any other operation, so
     * the records in the physical .db file can be placed in the Map that keeps
     * them in memory; I also have a method called persistDbRecords, which
     * writes each record back to the physical .db file, but this test aims only
     * to test the functionalities without altering the database, so this method
     * is never called anywhere
     */

    public static void main(String[] args) throws IOException {
        try {
            Files.copy(Paths.get(ORIGINAL_DATABASE), Paths.get(COPY_DATABASE), StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING);
            data = new Data(COPY_DATABASE);
            new ConcurrencyTest().startTests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startTests() {
        try {

            /*
             * Practically, it is not necessary to execute this loop more than 1
             * time, but if you want, you can increase the controller variable,
             * so it is executed as many times as you want
             */
            for (int i = 0; i < 10000; i++) {
                Thread updatingRandom = new UpdatingRandomRecordThread();
                updatingRandom.start();
                Thread updatingRecord1 = new UpdatingRecord1Thread();
                updatingRecord1.start();
                Thread creatingRecord = new CreatingRecordThread();
                creatingRecord.start();
                Thread deletingRecord = new DeletingRecord1Thread();
                deletingRecord.start();
                Thread findingRecords = new FindingRecordsThread();
                findingRecords.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class UpdatingRandomRecordThread extends Thread {

        @SuppressWarnings("deprecation")
        public void run() {

            String[] record = {"Damo", "Phoenix", "Plumbing", "1", "$100", ""};

            final int recNo = (int) (Math.random() * 50);
            long cookie = 0;
            try {
                System.out.println(Thread.currentThread().getId()
                        + " trying to lock record #" + recNo
                        + " on UpdatingRandomRecordThread");

                /*
                 * The generated record number may not exist in the database, so
                 * a RecordNotFoundException must be thrown by the lock method.
                 * Since the database records are in a cache, it is not
                 * necessary to put the unlock instruction in a finally block,
                 * because an exception can only occur when calling the lock
                 * method (not when calling the update/delete methods),
                 * therefore it is not necessary to call the unlock method in a
                 * finally block, but you can customize this code according to
                 * your reality
                 */
                cookie = data.lock(recNo);
                System.out.println(Thread.currentThread().getId()
                        + " trying to update record #" + recNo
                        + " on UpdatingRandomRecordThread");

                /*
                 * An exception cannot occur here, otherwise, the unlock
                 * instruction will not be reached, and the record will be
                 * locked forever. In this case, I created a class called
                 * RoomRetriever, which transforms from Room to String array,
                 * and vice-versa, but it could also be done this way:
                 *
                 * data.update(recNo, new String[] {"Palace", "Smallville", "2",
                 * "Y", "$150.00", "2005/07/27", null});
                 */
                data.update(recNo, record, cookie);
                System.out.println(Thread.currentThread().getId()
                        + " trying to unlock record #" + recNo
                        + " on UpdatingRandomRecordThread");

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    data.unlock(recNo, cookie);
                } catch (RecordNotFoundException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class UpdatingRecord1Thread extends Thread {

        @SuppressWarnings("deprecation")
        public void run() {

            String[] record = {"Kody", "Tempe", "Heating", "1", "$200", ""};
            long cookie = 0;
            try {
                System.out.println(Thread.currentThread().getId()
                        + " trying to lock record #1 on"
                        + " UpdatingRecord1Thread");
                cookie = data.lock(1);
                System.out.println(Thread.currentThread().getId()
                        + " trying to update record #1 on"
                        + " UpdatingRecord1Thread");
                data.update(1, record, cookie);
                System.out.println(Thread.currentThread().getId()
                        + " trying to unlock record #1 on"
                        + "UpdatingRecord1Thread");

                /*
                 * In order to see the deadlock, this instruction can be
                 * commented, and the other Threads, waiting to update/delete
                 * record #1 will wait forever and the deadlock will occur
                 */

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    data.unlock(1, cookie);
                } catch (RecordNotFoundException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class CreatingRecordThread extends Thread {

        @SuppressWarnings("deprecation")
        public void run() {

            String[] record = {"Charlie", "Athlone", "Barking", "1", "$300", ""};

            try {
                System.out.println(Thread.currentThread().getId()
                        + " trying to create a record");
                int recNo = data.create(record);
                System.out.println(Thread.currentThread().getId()
                        + " record created. recId: " + recNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DeletingRecord1Thread extends Thread {

        public void run() {
            long cookie = 0;
            try {
                System.out.println(Thread.currentThread().getId()
                        + " trying to lock record #1 on "
                        + "DeletingRecord1Thread");
                cookie = data.lock(1);
                System.out.println(Thread.currentThread().getId()
                        + " trying to delete record #1 on "
                        + "DeletingRecord1Thread");
                data.delete(1, cookie);
                System.out.println(Thread.currentThread().getId()
                        + " trying to unlock record #1 on "
                        + "DeletingRecord1Thread");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    data.unlock(1, cookie);
                } catch (RecordNotFoundException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class FindingRecordsThread extends Thread {

        public void run() {
            try {
                System.out.println(Thread.currentThread().getId()
                        + " trying to find records");
                final String[] criteria = {null, "Phoenix", null, null,
                        null, null, null, null};
                final int[] results = data.find(criteria);

                for (int i = 0; i < results.length; i++) {
                    System.out.println(results.length + " results found.");
                    try {
                        final String message = Thread.currentThread().getId()
                                + " going to read record #" + results[i]
                                + " in FindingRecordsThread - still "
                                + ((results.length - 1) - i) + " to go.";
                        System.out.println(message);
                        final String[] contractor = data.read(results[i]);
                        System.out.println("Contractor (FindingRecordsThread): "
                                + contractor[0]);
                        System.out.println("Has next? "
                                + (i < (results.length - 1)));
                    } catch (Exception e) {
                        /*
                         * In case a record was found during the execution of
                         * the find method, but deleted before the execution of
                         * the read instruction, a RecordNotFoundException will
                         * occur, which would be normal then
                         */
                        System.out.println("Exception in "
                                + "FindingRecordsThread - " + e);
                    }
                }
                System.out.println("Exiting for loop");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}