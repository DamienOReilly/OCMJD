package suncertify.application;

import suncertify.remote.RemoteContractorService;
import suncertify.utils.PropertiesManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Damien O'Reilly
 */
public class ServiceFactory {

    /**
     * Logger instance.
     */
    private static Logger logger = Logger.getLogger("suncertify.application");

    private ServiceFactory() {
    }

    public static ContractorService getService(ApplicationMode mode) throws RemoteException, NotBoundException {
        ContractorService contractorService = null;
        switch (mode) {
            case NETWORK:
                logger.log(Level.INFO, "Creating remote ContractorService.");
                contractorService = getRemoteContractorService();
                break;
            case ALONE:
                logger.log(Level.INFO, "Creating local ContractorService.");
                contractorService = getContractorService();
                break;
            default:
                logger.log(Level.WARNING, "Invalid mode specified, defaulting to local ContractorService.");
                contractorService = getContractorService();
        }
        return contractorService;
    }

    private static ContractorService getContractorService() {
        String dbPath = PropertiesManager.getInstance().getProperty("dbpath");
        return new ContractorServiceImpl(dbPath);
    }

    private static ContractorService getRemoteContractorService() throws RemoteException, NotBoundException {
        String hostname = PropertiesManager.getInstance().getProperty("hostname");
        int port = Registry.REGISTRY_PORT;
        logger.log(Level.INFO, "Using hostname: " + hostname + " and port: " + port);

        Registry registry = LocateRegistry.getRegistry(hostname);
        return (ContractorService) registry.lookup(RemoteContractorService.SERVER_NAME);
    }
}