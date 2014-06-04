package suncertify.remote;

import suncertify.application.ContractorService;
import suncertify.common.Constants;

import java.rmi.Remote;

/**
 * This interface is to enable {@link suncertify.application.ContractorService} method calls over RMI.
 *
 * @author Damien O'Reilly
 */
public interface RemoteContractorService extends Remote, ContractorService {

    /**
     * RMI server name for usage with this application.
     */
    public static final String SERVER_NAME = Constants.APPLICATION_NAME + "_server";
}
