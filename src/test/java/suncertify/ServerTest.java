package suncertify;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import suncertify.application.ContractorService;
import suncertify.common.Contractor;
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

    private Registry registry;
    private ContractorService service;

    @Before
    public void setUp() throws RemoteException, NotBoundException {
        registry = LocateRegistry.getRegistry("localhost");
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
            RecordNotFoundException {
        service.bookContractor(new Contractor(5, "aa", "bb", "cc", "dd", "ee", ""), "00000200");
        List<Contractor> contractors = service.search(new String[1]);
        for (Contractor contractor : contractors) {
            System.out.println(contractor.toString());
        }
    }

}