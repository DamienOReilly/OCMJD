package suncertify.application;

/**
 * Exception class to handle when a Contractor has already been booked.
 *
 * @author Damien O'Reilly
 */
public class ContractorUnavailableException extends Exception {

    /**
     * Constructs a ContractorUnavailableException with no message.
     */
    public ContractorUnavailableException() {
    }

    /**
     * Constructs a ContractorUnavailableException with an informative message.
     *
     * @param message Informative message.
     */
    public ContractorUnavailableException(String message) {
        super(message);
    }
}
