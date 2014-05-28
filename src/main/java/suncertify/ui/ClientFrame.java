package suncertify.ui;

import suncertify.application.ApplicationMode;
import suncertify.common.Constants;

import javax.swing.*;

/**
 * @author Damien O'Reilly
 */
public class ClientFrame {

    private JFrame clientFrame;

    private ClientController clientController;

    private JTable table;

    public ClientFrame() {
        clientFrame = new JFrame();
        clientFrame.setTitle(Constants.APPLICATION_NAME + " - " + Constants.APPLICATION_VERSION);
        clientFrame.setSize(1000, 700);
        clientFrame.setLocationRelativeTo(null);
        clientFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        clientFrame.setResizable(true);
        ConfigurationDialog configurationDialog = new ConfigurationDialog(ApplicationMode.NETWORK);
        clientController = new ClientController();

        init();

    }

    private void init() {
        JPanel panel = new JPanel(new BoxLayout(clientFrame, BoxLayout.Y_AXIS));
        panel.add(tablePanel());
    }

    private JScrollPane tablePanel() {

        return null;
    }

    public static void main(String[] args) {
        ClientFrame clientFrame = new ClientFrame();
    }
}
