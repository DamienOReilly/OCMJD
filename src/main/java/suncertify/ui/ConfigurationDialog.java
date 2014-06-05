package suncertify.ui;

import suncertify.application.ApplicationMode;
import suncertify.utils.Constants;
import suncertify.utils.PropertiesManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

/**
 * This class displays a dialog to the user asking them to input settings depending on application launch mode.
 *
 * @author Damien O'Reilly
 */
public class ConfigurationDialog {

    private JDialog dialog;

    private final PropertiesManager propertiesManager = PropertiesManager.getInstance();

    /**
     * Configuration text fields.
     */
    private final JTextField databasePath = new JTextField(20);
    private final JTextField hostnameField = new JTextField(15);

    /**
     * Instance to identify application launch mode.
     */
    private final ApplicationMode mode;

    /**
     * Button labels.
     */
    private static final String CHOOSE = "Select file...";
    private static final String OK = "OK";
    private static final String EXIT = "Exit";

    /**
     * Action listener for the OK button.
     */
    private ConfigActionListener configActionListener;

    /**
     * GridBagLayout constraints setup.
     */
    private final GridBagConstraints gbc = new GridBagConstraints();
    private int rowCount = 0;

    /**
     * Constructor that takes in application launch mode.
     *
     * @param mode Launch mode.
     */
    public ConfigurationDialog(ApplicationMode mode) {
        this.mode = mode;
    }

    /**
     * Add components to this dialog and display it to the user.
     */
    public void init() {
        configActionListener = new ConfigActionListener();
        dialog = new JDialog();

        // Exit the VM if user clicks close on dialog window header.
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        dialog.setTitle("Configure " + Constants.APPLICATION_NAME);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        dialog.getRootPane().setBorder(new EmptyBorder(5, 5, 5, 5));
        dialog.setLayout(new GridBagLayout());

        gbc.insets = new Insets(2, 2, 2, 2);

        if ((mode == ApplicationMode.ALONE) || (mode == ApplicationMode.SERVER)) {
            getDatabasePanel();
        }
        if (mode == ApplicationMode.NETWORK) {
            getNetworkPanel();
        }
        getNavigationPanel();

        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Setup the database chooser panel.
     */
    private void getDatabasePanel() {
        JLabel databasePathLabel = new JLabel("Database path:");

        JButton selectFileButton = new JButton(CHOOSE);
        selectFileButton.addActionListener(configActionListener);
        selectFileButton.setMnemonic(KeyEvent.VK_S);
        selectFileButton.setToolTipText("Select location to database file.");

        databasePath.setText(propertiesManager.getProperty("dbpath", ""));
        databasePath.setToolTipText("Path to the database file.");

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = rowCount;
        dialog.add(databasePathLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = rowCount;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(databasePath, gbc);

        gbc.gridx = 2;
        gbc.gridy = rowCount;
        dialog.add(selectFileButton, gbc);

        gbc.fill = GridBagConstraints.NONE;

        rowCount++;
    }

    /**
     * Setup the network configuration panel.
     */
    private void getNetworkPanel() {
        JLabel hostnameLabel = new JLabel("Hostname:");
        hostnameField.setText(propertiesManager.getProperty("hostname", "localhost"));
        hostnameField.setToolTipText("Hostname of the server running the database.");

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = rowCount;
        dialog.add(hostnameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = rowCount;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        dialog.add(hostnameField, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        rowCount++;
    }

    /**
     * Setup the navigation buttons panel.
     */
    private void getNavigationPanel() {
        JButton cancelButton = new JButton(EXIT);
        cancelButton.addActionListener(configActionListener);
        cancelButton.setMnemonic(KeyEvent.VK_E);
        cancelButton.setToolTipText("Exit the application.");

        JButton okButton = new JButton(OK);
        okButton.addActionListener(configActionListener);
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.setToolTipText("Accept configuration and start.");

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        panel.add(cancelButton, gbc);
        panel.add(okButton, gbc);

        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 2;
        gbc.gridy = rowCount;
        dialog.add(panel, gbc);

        gbc.fill = GridBagConstraints.NONE;

        rowCount++;
    }

    /**
     * Displays a file chooser dialog to enable the user to select a database file to use.
     */
    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle("Please locate the database to use with this application.");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Database file (*.db)", "db");
        fileChooser.setFileFilter(filter);
        int ret = fileChooser.showOpenDialog(dialog);
        if (ret == JFileChooser.APPROVE_OPTION) {
            databasePath.setText(fileChooser.getSelectedFile().toString());
        }
    }

    /**
     * Action Listener inner class to add functionality to the navigation buttons.
     */
    private class ConfigActionListener implements ActionListener {

        /**
         * Invoked when an action occurs.
         * <p/>
         * <ul>
         * <li><code>CHOOSE</code> calls {@link ConfigurationDialog#selectFile()} to select a file.</li>
         * <li><code>OK</code> calls {@link ConfigurationDialog#persistProperties()} to persist inputted
         * properties and open the next <code>JFrame</code></li>
         * <li><code>EXIT</code> to exit the VM.</li>
         * </ul>
         *
         * @param e Action
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            String command = e.getActionCommand();

            switch (command) {
                case CHOOSE:
                    selectFile();
                    break;
                case OK:
                    persistProperties();
                    dialog.dispose();
                    break;
                case EXIT:
                    System.exit(0);
            }
        }
    }

    /**
     * Save the user inputted settings to a persisted file.
     */
    private void persistProperties() {
        if ((mode == ApplicationMode.ALONE) || (mode == ApplicationMode.SERVER)) {
            propertiesManager.setProperty("dbpath", databasePath.getText());
        }
        if (mode == ApplicationMode.NETWORK) {
            propertiesManager.setProperty("hostname", hostnameField.getText());
        }
    }
}