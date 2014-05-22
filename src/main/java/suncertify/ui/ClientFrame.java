package suncertify.ui;

import suncertify.application.ApplicationMode;
import suncertify.common.Constants;

import javax.swing.*;

/**
 * @author Damien O'Reilly
 */
public class ClientFrame {

    private JFrame mainFrame = new JFrame();

    private ConfigurationDialog configurationDialog;

    private ClientController clientController;

    public ClientFrame() {
        mainFrame.setTitle(Constants.APPLICATION_NAME + " - " + Constants.APPLICATION_VERSION);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        configurationDialog = new ConfigurationDialog(ApplicationMode.NETWORK);

        clientController = new ClientController();

    }

    public static void main(String[] args) {
        ClientFrame clientFrame = new ClientFrame();
    }
}
