package suncertify.ui;

import suncertify.common.Constants;
import suncertify.remote.Server;
import suncertify.utils.PropertiesManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * @author Damien O'Reilly
 */
public class ServerFrame {

    private JButton shutdownButton;

    private JButton startButton;

    private JLabel statusLabel;

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

    private JPanel mainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(dbPanel(), BorderLayout.NORTH);
        mainPanel.add(serverPanel(), BorderLayout.CENTER);
        mainPanel.add(statusPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

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

    private JPanel dbPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dbPathLabel = new JLabel("Database:");
        JTextField dbPathField = new JTextField(PropertiesManager.getInstance().getProperty("dbpath"));
        dbPathField.setEditable(false);
        panel.add(dbPathLabel);
        panel.add(dbPathField);

        return panel;
    }

    private JPanel statusPanel() {
        JPanel panel = new JPanel();
        Border inside = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        Border outside = BorderFactory.createEmptyBorder(0, 5, 5, 5);
        panel.setBorder(BorderFactory.createCompoundBorder(outside, inside));
        statusLabel = new JLabel("Server is NOT STARTED.");
        panel.add(statusLabel);

        return panel;
    }


    private class ShutdownListener implements ActionListener {
        /**
         * Invoked when an action occurs.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private class StartListener implements ActionListener {
        /**
         * Invoked when an action occurs.
         *
         * @param e
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