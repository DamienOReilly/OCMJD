package suncertify.application;

import suncertify.common.Contractor;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface will allow clients to connect and interact with the server.
 *
 * @author Damien O'Reilly
 */
public interface ContractorService {

    public void bookContractor(Contractor contractor) throws ContractorUnavailableException, RemoteException,
            RecordNotFoundException, suncertify.db.SecurityException;

    public List<Contractor> search(String[] criteria) throws RemoteException;

    void unbookContractor(Contractor contractor) throws RemoteException, RecordNotFoundException, SecurityException;
}