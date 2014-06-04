package suncertify.ui;

import suncertify.application.ContractorService;
import suncertify.application.ContractorUnavailableException;
import suncertify.common.Contractor;
import suncertify.db.RecordNotFoundException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Damien O'Reilly
 */
public class ClientModel {

    private ContractorService service;

    public ClientModel(ContractorService service) {
        this.service = service;
    }

    public void bookContractor(Contractor contractor, String customerId) throws suncertify.db.SecurityException,
            RemoteException, RecordNotFoundException, ContractorUnavailableException {
        contractor.setOwner(customerId);
        service.bookContractor(contractor);
    }

    public void unbookContractor(Contractor contractor) throws suncertify.db.SecurityException,
            RemoteException, RecordNotFoundException {
        contractor.setOwner("");
        service.unbookContractor(contractor);
    }

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