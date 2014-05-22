package suncertify.ui;

import suncertify.application.ApplicationMode;
import suncertify.application.RemoteContractorService;
import suncertify.application.RemoteContractorServiceImpl;
import suncertify.common.Constants;
import suncertify.utils.PropertiesManager;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Damien O'Reilly
 */
public class ServerFrame {

    private JFrame mainFrame = new JFrame();

    private ConfigurationDialog configurationDialog;

    //private ClientController clientController;

    public ServerFrame() {
        mainFrame.setTitle(Constants.APPLICATION_NAME + " - " + Constants.APPLICATION_VERSION);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        configurationDialog = new ConfigurationDialog(ApplicationMode.SERVER);
        startServer();

        //clientController = new ClientController();

    }

    private void startServer() {
        try {
            RemoteContractorService service = new RemoteContractorServiceImpl(PropertiesManager.getInstance()
                    .getProperty("dbpath"));
            RemoteContractorService rmiStub = (RemoteContractorService) UnicastRemoteObject.exportObject(service,
                    Registry.REGISTRY_PORT);
            Registry registry = getRMIRegistry();
            registry.rebind(service.SERVER_NAME, rmiStub);
        } catch (RemoteException e) {
            //TODO: error, cannot start server.
            e.printStackTrace();
        }
    }

    private Registry getRMIRegistry() throws RemoteException {
        return LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
    }

    public static void main(String[] args) {
        ServerFrame serverFrame = new ServerFrame();
    }
}
