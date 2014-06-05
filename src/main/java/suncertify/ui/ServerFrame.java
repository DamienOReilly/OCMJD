package suncertify.ui;

import suncertify.remote.Server;
import suncertify.utils.Constants;
import suncertify.utils.PropertiesManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * This class creates a {@link javax.swing.JFrame} to allow the user to start the database server.
 *
 * @author Damien O'Reilly
 */
public class ServerFrame {

    /**
     * Server UI components.
     */
    private JButton shutdownButton;
    private JButton startButton;
    private JLabel statusLabel;

    /**
     * Default constructor. Sets up the frame.
     */
    public ServerFrame() {
        JFrame serverFrame = new JFrame();
        serverFrame.setTitle(Constants.APPLICATION_NAME + " - " + Constants.APPLICATION_VERSION);
        serverFrame.setSize(450, 200);
        serverFrame.setLocationRelativeTo(null);
        serverFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        serverFrame.setResizable(false);
        serverFrame.add(mainPanel());
        serverFrame.setVisible(true);
    }

    /**
     * Build the main panel that will hold the individual panels below.
     *
     * @return Main panel.
     */
    private JPanel mainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(dbPanel(), BorderLayout.NORTH);
        mainPanel.add(serverPanel(), BorderLayout.CENTER);
        mainPanel.add(statusPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Build the server panel with buttons to start/stop the server.
     *
     * @return Server panel.
     */
    private JPanel serverPanel() {
        JPanel panel = new JPanel();

        Border inside = BorderFactory.createTitledBorder("Server");
        Border outside = BorderFactory.createEmptyBorder(0, 5, 5, 5);
        panel.setBorder(BorderFactory.createCompoundBorder(outside, inside));

        shutdownButton = new JButton("Shutdown");
        shutdownButton.setToolTipText("Shuts down and exits the server.");
        shutdownButton.setMnemonic(KeyEvent.VK_D);
        shutdownButton.setEnabled(false);
        shutdownButton.addActionListener(new ShutdownListener());

        startButton = new JButton("Start");
        startButton.setToolTipText("Starts the server to listen for incoming connections.");
        startButton.setMnemonic(KeyEvent.VK_S);
        startButton.addActionListener(new StartListener());

        panel.add(shutdownButton);
        panel.add(startButton);

        return panel;
    }

    /**
     * Build the database path info panel.
     *
     * @return Database path info panel.
     */
    private JPanel dbPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dbPathLabel = new JLabel("Database:");
        JTextField dbPathField = new JTextField(PropertiesManager.getInstance().getProperty("dbpath"));
        dbPathField.setEditable(false);
        panel.add(dbPathLabel);
        panel.add(dbPathField);

        return panel;
    }

    /**
     * Build the server status panel.
     *
     * @return Server status panel.
     */
    private JPanel statusPanel() {
        JPanel panel = new JPanel();
        Border inside = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        Border outside = BorderFactory.createEmptyBorder(0, 5, 5, 5);
        panel.setBorder(BorderFactory.createCompoundBorder(outside, inside));
        statusLabel = new JLabel("Server is NOT STARTED.");
        panel.add(statusLabel);

        return panel;
    }

    /**
     * Class to handle when the user clicks the stop server button on the Server UI.
     */
    private class ShutdownListener implements ActionListener {
        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * Class to handle when the user clicks the start server button on the Server UI.
     */
    private class StartListener implements ActionListener {
        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Server.start();
            if (Server.isStarted()) {
                startButton.setEnabled(false);
                shutdownButton.setEnabled(true);
                statusLabel.setText("Server is STARTED.");
            }
        }
    }
}