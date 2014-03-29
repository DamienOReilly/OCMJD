package suncertify.db;

/**
 * Exception for handling when an attempt to create with a record with an already existing key.
 *
 * @author Damien O'Reilly
 */
public class DuplicateKeyException extends Exception {

    /**
     * Constructs a DuplicateKeyException with no message.
     */
    public DuplicateKeyException() {
    }

    /**
     * Constructs a DuplicateKeyException with an informative message.
     *
     * @param message
     *         Informative message
     */
    public DuplicateKeyException(String message) {
        super(message);
    }
}
