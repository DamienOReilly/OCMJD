package suncertify.application;

import suncertify.common.Contractor;
import suncertify.db.DB;
import suncertify.db.DatabaseFactory;
import suncertify.db.RecordNotFoundException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Damien O'Reilly
 */
public class ContractorServiceImpl implements ContractorService {

    // TODO: protected to assist unit test
    protected DB database;

    public ContractorServiceImpl(String databasePath) {
        database = DatabaseFactory.getDatabase(databasePath);
    }

    @Override
    public void bookContractor(Contractor contractor, String customerId) throws RecordNotFoundException,
            suncertify.db.SecurityException {
        long lockCookie = 0;
        try {
            lockCookie = database.lock((contractor.getRecordId()));
            contractor.setOwner(customerId);
            database.update(contractor.getRecordId(), contractor.asArray(), lockCookie);
        } finally {
            database.unlock(contractor.getRecordId(), lockCookie);
        }
    }

    @Override
    public List<Contractor> search(String[] criteria) throws RemoteException {

        List<Contractor> foundResults = new ArrayList<>();
        int[] recordIds = database.find(criteria);
        for (int recordId : recordIds) {
            try {
                Contractor contractor = read(recordId);
                foundResults.add(contractor);
            } catch (RecordNotFoundException e) {
                // Record has been deleted since retrieving list of record id's matching criteria, so ignore and
                // move on.
            }
        }
        return foundResults;
    }

    private Contractor read(int recordId) throws RecordNotFoundException {
        Contractor contractor = new Contractor(recordId, database.read(recordId));
        return contractor;
    }
}