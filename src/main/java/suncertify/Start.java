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
 * @author Damien O'Reilly
 */
public class Start {

    /**
     * Logger instance.
     */
    private static Logger logger = Logger.getLogger("suncertify");

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
                ClientFrame view = new ClientFrame(service);
                ClientController controller = new ClientController(model, view);
                view.init();
            } catch (RemoteException | NotBoundException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                MsgBox.showErrorAndExit(e.getMessage());
            }
        } else if (mode == ApplicationMode.SERVER) {
            ConfigurationDialog configurationDialog = new ConfigurationDialog(ApplicationMode.SERVER);
            configurationDialog.init();
            ServerFrame view = new ServerFrame();
        }
    }

    private static void printHelp(String[] args) {
        System.err.println("Invalid parameter(s): "
                + Arrays.toString(args));
        System.err.println("Usage: ");
        System.err.println("java -jar runme.jar \"server\" - server");
        System.err.println("java -jar runme.jar \"alone\"  - non-networked client");
        System.err.println("java -jar runme.jar            - client");
    }
}
