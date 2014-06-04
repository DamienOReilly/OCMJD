package suncertify.remote;

import suncertify.application.ContractorServiceImpl;

/**
 * Provides a remote compatible implementation of the business layer that can be used over RMI.
 *
 * @author Damien O'Reilly
 */
public class RemoteContractorServiceImpl extends ContractorServiceImpl implements RemoteContractorService {

    /**
     * Constructor taking in database file location.
     *
     * @param databasePath Database location.
     */
    public RemoteContractorServiceImpl(String databasePath) {
        super(databasePath);
    }
}
