package suncertify.application;

import suncertify.common.Contractor;
import suncertify.db.RecordNotFoundException;

import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface will allow clients to connect and interact with the server.
 *
 * @author Damien O'Reilly
 */
public interface ContractorService {

    public void bookContractor(Contractor contractor, String customerId) throws RecordNotFoundException,
            suncertify.db.SecurityException, RemoteException;

    public List<Contractor> search(String[] criteria) throws RemoteException;

}