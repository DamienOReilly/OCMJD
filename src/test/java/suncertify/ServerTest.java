package suncertify;

import org.junit.Test;
import suncertify.application.ContractorService;
import suncertify.application.RemoteContractorService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Damien O'Reilly
 */
public class ServerTest {

    @Test
    public void testRMIConnectionToServer() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost");
        ContractorService service = (ContractorService) registry.lookup(RemoteContractorService.SERVER_NAME);
    }


}
