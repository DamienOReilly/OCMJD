package suncertify.remote;

import suncertify.utils.MsgBox;
import suncertify.utils.PropertiesManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Damien O'Reilly
 */
public class Server {

    /**
     * Logger instance.
     */
    private static Logger logger = Logger.getLogger("suncertify.remote");

    private static boolean started = false;

    private Server() {
    }

    public static void start() {
        if (started) {
            logger.log(Level.WARNING, "Server is already started.");
        } else {
            try {
                RemoteContractorService service = new RemoteContractorServiceImpl(PropertiesManager.getInstance()
                        .getProperty("dbpath"));
                RemoteContractorService rmiStub = (RemoteContractorService) UnicastRemoteObject.exportObject(service,
                        Registry.REGISTRY_PORT);
                Registry registry = getRMIRegistry();
                registry.rebind(RemoteContractorService.SERVER_NAME, rmiStub);
                logger.log(Level.INFO, "Server has been started. Listening on port: " + Registry.REGISTRY_PORT);
                started = true;
            } catch (RemoteException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                MsgBox.showErrorAndExit(e.getMessage());
            }
        }
    }

    private static Registry getRMIRegistry() throws RemoteException {
        return LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
    }

    public static boolean isStarted() {
        return started;
    }
}
