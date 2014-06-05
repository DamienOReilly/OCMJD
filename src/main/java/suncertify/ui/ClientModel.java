package suncertify.ui;

import suncertify.application.Contractor;
import suncertify.application.ContractorService;
import suncertify.application.ContractorUnavailableException;
import suncertify.db.RecordNotFoundException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the model in the client UI. It interacts with the business layer.
 *
 * @author Damien O'Reilly
 */
public class ClientModel {

    /**
     * Instance of the business layer.
     */
    private final ContractorService service;

    /**
     * Constructor that accepts a reference to the business layer.
     *
     * @param service Business layer service.
     */
    public ClientModel(ContractorService service) {
        this.service = service;
    }

    /**
     * Allows a particular contractor to be booked.
     *
     * @param contractor Contractor to book.
     * @param customerId Customer ID to book against.
     * @throws RemoteException                 Problem communicating with the server.
     * @throws RecordNotFoundException         Record doesn't exist.
     * @throws ContractorUnavailableException  Contractor is already booked.
     */
    public void bookContractor(Contractor contractor, String customerId) throws RemoteException,
            RecordNotFoundException, ContractorUnavailableException {
        contractor.setOwner(customerId);
        service.bookContractor(contractor);
    }

    /**
     * Allows a particular contractor to be un-booked.
     *
     * @param contractor Contractor to un-book.
     * @throws RemoteException                 Problem communicating with the server.
     * @throws RecordNotFoundException         Record doesn't exist.
     */
    public void unbookContractor(Contractor contractor) throws RemoteException, RecordNotFoundException {
        contractor.setOwner("");
        service.unbookContractor(contractor);
    }

    /**
     * Search for a contractor in the database based on a search criteria.
     *
     * @param criteria Search criteria.
     * @return List of contractors found.
     * @throws RemoteException Problem communicating with the server.
     */
    public List<Contractor> search(String[] criteria) throws RemoteException {
        List<Contractor> returnedResults = service.search(criteria);
        List<Contractor> exactMatchResults = new ArrayList<>();

        for (Contractor contractor : returnedResults) {
            if (exactMatch(contractor, criteria)) {
                exactMatchResults.add(contractor);
            }
        }
        return exactMatchResults;
    }

    /**
     * Checks if the search result returned from the database layer exactly matches the search criteria.
     *
     * @param contractor Contractor to check.
     * @param criteria   Search criteria.
     * @return True if exactly matches, otherwise false.
     */
    private boolean exactMatch(Contractor contractor, String[] criteria) {
        final String[] contractorArray = contractor.asArray();
        for (int i = 0; i < criteria.length; i++) {
            if ((criteria[i] != null) && (!criteria[i].equals("")) && (!contractorArray[i].equals(criteria[i]))) {
                return false;
            }
        }
        return true;
    }
}