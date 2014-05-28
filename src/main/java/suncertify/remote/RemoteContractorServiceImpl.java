package suncertify.remote;

import suncertify.application.ContractorServiceImpl;

/**
 * @author Damien O'Reilly
 */
public class RemoteContractorServiceImpl extends ContractorServiceImpl implements RemoteContractorService {

    public RemoteContractorServiceImpl(String databasePath) {
        super(databasePath);
    }
}
