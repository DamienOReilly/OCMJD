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
 * This class acts as a factory and provides either a local or remote instance on a
 * {@link suncertify.application.ContractorService} depending on application launch mode.
 *
 * @author Damien O'Reilly
 */
public class ServiceFactory {

    /**
     * Logger instance.
     */
    private static final Logger logger = Logger.getLogger("suncertify.application");

    /**
     * Private to prevent class instantiation.
     */
    private ServiceFactory() {
    }

    /**
     * Gets a {@link suncertify.application.ContractorService} based on specified application launch mode.
     *
     * @param mode Application launch mode.
     * @return A local or remote {@code ContractorService}
     * @throws RemoteException   Problem contacting the server.
     * @throws NotBoundException Problem contacting the server.
     */
    public static ContractorService getService(ApplicationMode mode) throws RemoteException, NotBoundException {
        ContractorService contractorService;
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

    /**
     * Returns a local service.
     *
     * @return Local service.
     */
    private static ContractorService getContractorService() {
        String dbPath = PropertiesManager.getInstance().getProperty("dbpath");
        return new ContractorServiceImpl(dbPath);
    }

    /**
     * Returns a remote service.
     *
     * @return Remote service.
     * @throws RemoteException   Problem contacting the server.
     * @throws NotBoundException Problem contacting the server.
     */
    private static ContractorService getRemoteContractorService() throws RemoteException, NotBoundException {
        String hostname = PropertiesManager.getInstance().getProperty("hostname");
        int port = Registry.REGISTRY_PORT;
        logger.log(Level.INFO, "Using hostname: " + hostname + " and port: " + port);

        Registry registry = LocateRegistry.getRegistry(hostname);
        return (ContractorService) registry.lookup(RemoteContractorService.SERVER_NAME);
    }
}