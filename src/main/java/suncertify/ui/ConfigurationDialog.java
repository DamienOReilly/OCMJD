package suncertify.ui;

import suncertify.application.ApplicationMode;
import suncertify.common.Constants;
import suncertify.utils.PropertiesManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * @author Damien O'Reilly
 */
public class ConfigurationDialog {

    private JDialog dialog;

    private PropertiesManager propertiesManager = PropertiesManager.getInstance();

    /**
     * The text field for the path to the database file.
     */
    private JTextField databasePath = new JTextField(30);

    private static final String CHOOSE = "Select file...";
    private static final String OK = "OK";
    private static final String EXIT = "Exit";

    private ConfigActionListener configActionListener;


    public ConfigurationDialog(ApplicationMode mode) {
        configActionListener = new ConfigActionListener();
        dialog = new JDialog();
        dialog.setTitle("Configure " + Constants.APPLICATION_NAME);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);

        dialog.add(getDatabasePanel(), BorderLayout.NORTH);
        dialog.add(getNavigationPanel(), BorderLayout.SOUTH);
        dialog.pack();

        dialog.setVisible(true);
    }


    private JPanel getDatabasePanel() {
        JPanel panel = new JPanel();

        //Database path
        JLabel databasePathLabel = new JLabel("Database path: ");
        JButton selectFileButton = new JButton(CHOOSE);
        selectFileButton.addActionListener(configActionListener);
        selectFileButton.setMnemonic(KeyEvent.VK_S);
        databasePath.setText(propertiesManager.getProperty("dbpath", ""));

        panel.add(databasePathLabel);
        panel.add(databasePath);
        panel.add(selectFileButton);

        return panel;
    }

    private JPanel getNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton(EXIT);
        cancelButton.addActionListener(configActionListener);
        cancelButton.setMnemonic(KeyEvent.VK_E);

        JButton okButton = new JButton(OK);
        okButton.addActionListener(configActionListener);
        okButton.setMnemonic(KeyEvent.VK_O);

        panel.add(cancelButton);
        panel.add(okButton);

        return panel;
    }


    //Test
//    public static void main(String[] args) {
//        ConfigurationDialog configurationDialog = new ConfigurationDialog();
//    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setDialogTitle("Please locate the database to use with this application.");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Database file", "db");
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
        propertiesManager.setProperty("dbpath", databasePath.getText());
    }

}