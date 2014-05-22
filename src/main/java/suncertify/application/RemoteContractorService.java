package suncertify.application;

import suncertify.common.Constants;

import java.rmi.Remote;

/**
 * @author Damien O'Reilly
 */
public interface RemoteContractorService extends Remote, ContractorService {

    public static final String SERVER_NAME = Constants.APPLICATION_NAME + "_server";
}
