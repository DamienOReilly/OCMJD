package suncertify.ui;

import suncertify.Constants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * @author Damien O'Reilly
 */
public class ConfigurationDialog {

    private JDialog dialog;

    /**
     * The text field for the path to the database file.
     */
    private JTextField databasePath = new JTextField(30);

    private static final String CHOOSE = "Select file...";
    private static final String OK = "OK";
    private static final String EXIT = "Exit";

    private ConfigActionListener configActionListener;


    public ConfigurationDialog() {
        configActionListener = new ConfigActionListener();
        dialog = new JDialog();
        dialog.setTitle("Configure " + Constants.APPLICATION_NAME);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

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
    public static void main(String[] args) {
        ConfigurationDialog configurationDialog = new ConfigurationDialog();
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Please locate the database to use with this application.");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Database", "db");
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
                case EXIT:
                    System.exit(0);
            }
        }
    }
}