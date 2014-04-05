package suncertify.db;

/**
 * Exception for handling when an attempt to modify a locked record with incorrect cookie.
 *
 * @author Damien O'Reilly
 */
public class SecurityException extends Exception {

    /**
     * Constructs a SecurityException with no message.
     */
    public SecurityException() {
    }

    /**
     * Constructs a SecurityException with an informative message.
     *
     * @param message Informative message
     */
    public SecurityException(String message) {
        super(message);
    }
}
