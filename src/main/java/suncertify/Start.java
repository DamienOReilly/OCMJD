package suncertify;

import suncertify.application.ApplicationMode;
import suncertify.application.ContractorService;
import suncertify.application.ServiceFactory;
import suncertify.ui.ClientController;
import suncertify.ui.ClientFrame;
import suncertify.ui.ClientModel;
import suncertify.ui.ConfigurationDialog;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 * @author Damien O'Reilly
 */
public class Start {

    public static void main(String[] args) {

        if (args.length == 0) {

        } else if (args[0].equalsIgnoreCase(ApplicationMode.ALONE.name())) {

            try {
                ConfigurationDialog configurationDialog = new ConfigurationDialog(ApplicationMode.ALONE);
                configurationDialog.init();

                ContractorService service = ServiceFactory.getService(ApplicationMode.ALONE);

                ClientModel model = new ClientModel(service);
                ClientFrame view = new ClientFrame(service);
                ClientController controller = new ClientController(model, view);

                view.init();

            } catch (RemoteException e) {
                //TODO
                e.printStackTrace();
            } catch (NotBoundException e) {
                //TODO
                e.printStackTrace();
            }

        } else if (args[1].equalsIgnoreCase(ApplicationMode.SERVER.name())) {

        } else {
            printHelp(args);
            System.exit(0);
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
