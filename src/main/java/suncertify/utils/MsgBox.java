package suncertify.utils;

import javax.swing.*;

/**
 * @author Damien O'Reilly
 */
public class MsgBox {

    public static void showWarning(final String msg) {
        JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(final String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorAndExit(final String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
