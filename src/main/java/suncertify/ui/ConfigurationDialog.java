package suncertify.ui;

import suncertify.application.ApplicationMode;
import suncertify.common.Constants;
import suncertify.utils.PropertiesManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Damien O'Reilly
 */
public class ConfigurationDialog {

    private JDialog dialog;

    private final PropertiesManager propertiesManager = PropertiesManager.getInstance();

    /**
     * The text field for the path to the database file.
     */
    private final JTextField databasePath = new JTextField(20);

    JTextField hostnameField = new JTextField(15);

    private ApplicationMode mode;

    private static final String CHOOSE = "Select file...";
    private static final String OK = "OK";
    private static final String EXIT = "Exit";

    private ConfigActionListener configActionListener;
    private GridBagConstraints gbc = new GridBagConstraints();
    private int rowCount = 0;


    public ConfigurationDialog(ApplicationMode mode) {
        this.mode = mode;
    }

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
        dialog.setLayout(new GridBagLayout());

        gbc.insets = new Insets(2, 2, 2, 2);

        if ((mode == ApplicationMode.ALONE) || (mode == ApplicationMode.SERVER)) {
            getDatabasePanel();
        }
        if ((mode == ApplicationMode.NETWORK) || (mode == ApplicationMode.SERVER)) {
            getNetworkPanel();
        }
        getNavigationPanel();

        dialog.pack();
        dialog.setVisible(true);
    }


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

        rowCount++;
    }

    private void getNetworkPanel() {
        JLabel hostnameLabel = new JLabel("Hostname:");
        hostnameField.setText(propertiesManager.getProperty("hostname", "localhost"));
        hostnameField.setToolTipText("Hostname of the server running the database.");

//        JLabel portLabel = new JLabel("Port:");
//        JTextField portField = new JTextField(6);
//        portField.setToolTipText("Post the server is listening on.");
//        portField.setText(propertiesManager.getProperty("port", String.valueOf(Registry.REGISTRY_PORT)));
//        PlainDocument doc = (PlainDocument) portField.getDocument();
//        doc.setDocumentFilter(new NumericDocumentFilter());

        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0;
        gbc.gridy = rowCount;
        dialog.add(hostnameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = rowCount;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(hostnameField, gbc);

//        rowCount++;
//
//        gbc.gridx = 0;
//        gbc.gridy = rowCount;
//        dialog.add(portLabel, gbc);
//
//        gbc.gridx = 1;
//        gbc.gridy = rowCount;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        dialog.add(portField, gbc);

        gbc.fill = GridBagConstraints.NONE;

        rowCount++;
    }

    private void getNavigationPanel() {
        JButton cancelButton = new JButton(EXIT);
        cancelButton.addActionListener(configActionListener);
        cancelButton.setMnemonic(KeyEvent.VK_E);
        cancelButton.setToolTipText("Exit the application.");

        JButton okButton = new JButton(OK);
        okButton.addActionListener(configActionListener);
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.setToolTipText("Accept configuration and start.");

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(cancelButton, gbc);
        panel.add(okButton, gbc);

        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 2;
        gbc.gridy = rowCount;
        dialog.add(panel, gbc);

        rowCount++;
    }

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

    private class ConfigActionListener implements ActionListener {

        /**
         * Invoked when an action occurs.
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

    private void persistProperties() {
        if ((mode == ApplicationMode.ALONE) || (mode == ApplicationMode.SERVER)) {
            propertiesManager.setProperty("dbpath", databasePath.getText());
        }
        if ((mode == ApplicationMode.NETWORK) || (mode == ApplicationMode.SERVER)) {
            propertiesManager.setProperty("hostname", hostnameField.getText());
        }
    }

}