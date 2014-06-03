package suncertify.application;

/**
 * @author Damien O'Reilly
 */
public class ContractorUnavailableException extends Exception {

    public ContractorUnavailableException() {
    }

    public ContractorUnavailableException(String message) {
        super(message);
    }
}
