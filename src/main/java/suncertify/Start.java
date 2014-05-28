package suncertify;

import suncertify.application.ApplicationMode;
import suncertify.ui.ClientController;
import suncertify.ui.ClientFrame;
import suncertify.ui.ContractorModel;

import java.util.Arrays;

/**
 * @author Damien O'Reilly
 */
public class Start {

    public static void main(String[] args) {

        if (args.length == 0) {

        } else if (args[1].equalsIgnoreCase(ApplicationMode.ALONE.name())) {

            ContractorModel model = new ContractorModel();
            ClientFrame view = new ClientFrame();
            ClientController controller = new ClientController(model, view);

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
