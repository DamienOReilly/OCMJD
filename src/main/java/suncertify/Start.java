package suncertify;

import suncertify.application.ApplicationMode;
import suncertify.application.ContractorService;
import suncertify.application.ServiceFactory;
import suncertify.ui.*;
import suncertify.utils.MsgBox;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the main class of the application. It parses arguments passed on command line to determine the
 * different launch modes.
 *
 * @author Damien O'Reilly
 */
class Start {

    /**
     * Logger instance.
     */
    private static final Logger logger = Logger.getLogger("suncertify");

    /**
     * Entry point to the application.
     *
     * @param args Mode to determine how the application is launched.
     */
    public static void main(String[] args) {

        ApplicationMode mode = null;

        if (args.length == 0) {
            mode = ApplicationMode.NETWORK;
        } else if (args[0].equals("alone")) {
            mode = ApplicationMode.ALONE;
        } else if (args[0].equals("server")) {
            mode = ApplicationMode.SERVER;
        } else {
            printHelp(args);
            System.exit(0);
        }

        if (mode == ApplicationMode.NETWORK || mode == ApplicationMode.ALONE) {
            ConfigurationDialog configurationDialog = new ConfigurationDialog(mode);
            configurationDialog.init();

            try {
                ContractorService service = ServiceFactory.getService(mode);
                ClientModel model = new ClientModel(service);
                ClientFrame view = new ClientFrame(model);
                ClientController controller = new ClientController(model, view);
                view.init();
            } catch (RemoteException | NotBoundException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                MsgBox.showErrorAndExit(e.getMessage());
            }
        } else {
            ConfigurationDialog configurationDialog = new ConfigurationDialog(ApplicationMode.SERVER);
            configurationDialog.init();
            ServerFrame view = new ServerFrame();
        }
    }

    private static void printHelp(String[] args) {
        System.err.println("Invalid parameters: " + Arrays.toString(args));
        System.err.println("Usage: ");
        System.err.println("java -jar runme.jar server\t- server");
        System.err.println("java -jar runme.jar alone\t- non-networked client");
        System.err.println("java -jar runme.jar (no args)\t- client");
    }
}
