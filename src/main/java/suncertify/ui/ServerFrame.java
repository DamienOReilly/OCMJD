package suncertify.ui;

import suncertify.application.ApplicationMode;
import suncertify.common.Constants;
import suncertify.remote.Server;

import javax.swing.*;

/**
 * @author Damien O'Reilly
 */
public class ServerFrame {

    //private ClientController clientController;

    public ServerFrame() {
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle(Constants.APPLICATION_NAME + " - " + Constants.APPLICATION_VERSION);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        ConfigurationDialog configurationDialog = new ConfigurationDialog(ApplicationMode.SERVER);

        Server.start();
        //clientController = new ClientController();
    }

    public static void main(String[] args) {
        ServerFrame serverFrame = new ServerFrame();
    }
}