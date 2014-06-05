package suncertify;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import suncertify.application.Contractor;
import suncertify.application.ContractorService;
import suncertify.application.ContractorUnavailableException;
import suncertify.db.RecordNotFoundException;
import suncertify.remote.RemoteContractorService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * @author Damien O'Reilly
 */
@Ignore
public class ServerTest {

    private ContractorService service;

    @Before
    public void setUp() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost");
        service = (ContractorService) registry.lookup(RemoteContractorService.SERVER_NAME);
    }

    @Test
    public void testRMIConnectionToServerAndSearch() throws NotBoundException, RemoteException {
        List<Contractor> contractors = service.search(new String[1]);
        for (Contractor contractor : contractors) {
            System.out.println(contractor.toString());
        }
    }

    @Test
    public void testRMIConnectionToServerAndBook() throws suncertify.db.SecurityException, RemoteException,
            RecordNotFoundException, ContractorUnavailableException {
        Contractor contractor = new Contractor(5, new String[5]);
        contractor.setOwner("00000020");
        service.bookContractor(contractor);
        List<Contractor> contractors = service.search(new String[1]);
        for (Contractor c : contractors) {
            System.out.println(c.toString());
        }
    }

}