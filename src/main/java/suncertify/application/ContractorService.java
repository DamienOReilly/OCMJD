package suncertify.application;

import suncertify.db.RecordNotFoundException;

import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface will allow clients to connect and interact with the server.
 *
 * @author Damien O'Reilly
 */
public interface ContractorService {

    /**
     * Allows a contractor to be booked against a given customer id.
     *
     * @param contractor Contractor to book.
     * @throws ContractorUnavailableException  The contractor is already booked.
     * @throws RemoteException                 Problem communicating with the server.
     * @throws RecordNotFoundException         The requested record was not found.
     */
    public void bookContractor(Contractor contractor) throws ContractorUnavailableException, RemoteException,
            RecordNotFoundException;

    /**
     * Allows a contractor to be un-booked.
     *
     * @param contractor Contractor to un-book.
     * @throws RemoteException         RemoteException Problem communicating with the server.
     * @throws RecordNotFoundException The requested record was not found.
     */
    void unbookContractor(Contractor contractor) throws RemoteException, RecordNotFoundException;

    /**
     * Searches for contractors based on a given criteria.
     *
     * @param criteria Search criteria to search for a contractor or contractors.
     * @return {@link List} of {@link Contractor} matching criteria.
     * @throws RemoteException Problem communicating with the server.
     */
    public List<Contractor> search(String[] criteria) throws RemoteException;

}