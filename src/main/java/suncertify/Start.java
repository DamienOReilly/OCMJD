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
            logger.log(Level.INFO, "Launching network client.");
            mode = ApplicationMode.NETWORK;
        } else if (args[0].equals("alone")) {
            logger.log(Level.INFO, "Launching stand-alone mode.");
            mode = ApplicationMode.ALONE;
        } else if (args[0].equals("server")) {
            logger.log(Level.INFO, "Launching server.");
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
        System.err.println("Invalid parameter: " + Arrays.toString(args));
        System.err.println("Usage:\n"
                + "java -jar runme.jar server\t- server\n"
                + "java -jar runme.jar alone\t- stand-alone mode\n"
                + "java -jar runme.jar (no args)\t- network client\n");
    }
}
