package suncertify.application;

import suncertify.db.DB;
import suncertify.db.DatabaseFactory;
import suncertify.db.RecordNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ContractorService} to facilitate the business logic interaction with the database.
 *
 * @author Damien O'Reilly
 */
public class ContractorServiceImpl implements ContractorService {

    /**
     * Instance to database instance providing low level functions.
     */
    private final DB database;

    public ContractorServiceImpl(String databasePath) {
        database = DatabaseFactory.getDatabase(databasePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bookContractor(Contractor contractor) throws RecordNotFoundException,
            ContractorUnavailableException {

        if (alreadyBooked(contractor)) {
            throw new ContractorUnavailableException("Contractor is not available. Contractor may have booked " +
                    "already by another client.");
        }
        updateRecord(contractor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbookContractor(Contractor contractor) throws RecordNotFoundException {
        updateRecord(contractor);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Contractor> search(String[] criteria) {

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

    /**
     * Checks if a Contractor is already booked.
     *
     * @param contractor Contractor to check.
     * @return true if booked, otherwise false.
     * @throws RecordNotFoundException Record to check doesn't exist.
     */
    private boolean alreadyBooked(Contractor contractor) throws RecordNotFoundException {
        return !read(contractor.getRecordId()).getOwner().equals("");
    }

    /**
     * {@inheritDoc}
     */
    private void updateRecord(Contractor contractor) throws RecordNotFoundException {
        long lockCookie = 0;
        try {
            lockCookie = database.lock(contractor.getRecordId());
            database.update(contractor.getRecordId(), contractor.asArray(), lockCookie);
        } finally {
            database.unlock(contractor.getRecordId(), lockCookie);
        }
    }

    /**
     * Reads a Contractor from the database based on an id.
     *
     * @param recordId The record id of the record to read.
     * @return The Contractor.
     * @throws RecordNotFoundException If the record does not exits.
     */
    private Contractor read(int recordId) throws RecordNotFoundException {
        return new Contractor(recordId, database.read(recordId));
    }
}