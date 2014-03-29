package suncertify.db;

/**
 * Specific exception for handling when a record is not found.
 *
 * @author Damien O'Reilly
 */
public class RecordNotFoundException extends Exception {

    /**
     * Constructs a RecordNotFoundException with no message.
     */
    public RecordNotFoundException() {
    }

    /**
     * Constructs a RecordNotFoundException with an informative message.
     *
     * @param message
     *         Informative message
     */
    public RecordNotFoundException(String message) {
        super(message);
    }
}
