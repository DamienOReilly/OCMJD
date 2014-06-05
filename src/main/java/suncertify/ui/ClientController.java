package suncertify.ui;

import suncertify.application.Contractor;
import suncertify.application.ContractorUnavailableException;
import suncertify.db.RecordNotFoundException;
import suncertify.utils.MsgBox;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class acts as the Controller for the Client UI interface.
 *
 * @author Damien O'Reilly
 */
public class ClientController {

    /**
     * Client model and view instances.
     */
    private final ClientModel model;
    private final ClientFrame view;

    /**
     * Logger instance.
     */
    private static final Logger logger = Logger.getLogger("suncertify.ui");

    /**
     * Constructor that take in the client UI model and view. Sets up listeners for the UI components.
     *
     * @param model Model
     * @param view  View
     */
    public ClientController(ClientModel model, ClientFrame view) {
        this.model = model;
        this.view = view;

        view.addSearchListener(new SearchListener());
        view.addRefreshListener(new RefreshListener());
        view.addBookListener(new BookListener());
        view.addUnbookListener(new UnbookListener());
        view.getTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());
        view.getTable().getModel().addTableModelListener(new TableModelUpdateListener());
    }

    /**
     * Method to alert the user that a severe error occurred.
     *
     * @param ex Exception
     */
    private void showError(Exception ex) {
        logger.log(Level.SEVERE, ex.getMessage(), ex);
        MsgBox.showErrorAndExit(ex.getMessage());
    }

    /**
     * Method to alert the user that a non critical warning occurred.
     *
     * @param ex Exception
     */
    private void showWarning(Exception ex) {
        logger.log(Level.WARNING, ex.getMessage());
        MsgBox.showWarning(ex.getMessage());
    }

    /**
     * Updates the data on the table on the UI.
     *
     * @param criteria search criteria.
     * @throws RemoteException Problem communicating with the server.
     */
    private void updateTable(String[] criteria) throws RemoteException {
        List<Contractor> contractors = model.search(criteria);
        view.getTableModel().clearData();
        view.setNameSearchText("");
        view.setLocationSearchText("");
        view.getTableModel().updateData(contractors);
    }

    /**
     * Class to handle when the user clicks Search button on the Client UI.
     */
    private class SearchListener implements ActionListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] criteria = new String[]{view.getNameSearchText().trim(), view.getLocationSearchText().trim()};
            try {
                List<Contractor> contractors = model.search(criteria);
                view.getTableModel().clearData();
                view.getTableModel().updateData(contractors);
            } catch (RemoteException ex) {
                showError(ex);
            }
        }
    }

    /**
     * Class to handle when the user clicks the Refresh button on the Client UI.
     */
    private class RefreshListener implements ActionListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] criteria = new String[]{};
            try {
                updateTable(criteria);
            } catch (RemoteException ex) {
                showError(ex);
            }
        }
    }

    /**
     * Class to handle when the user clicks the Book button on the Client UI.
     */
    private class BookListener implements ActionListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            final String customerId = JOptionPane.showInputDialog(null, "Please enter a customer ID", "Book Contractor",
                    JOptionPane.QUESTION_MESSAGE);

            if (customerId != null) {
                if (customerId.matches("^\\d{8}$")) {
                    int row = view.getTable().getSelectedRow();
                    Contractor contractor = view.getTableModel().getContractor(row);
                    try {
                        model.bookContractor(contractor, customerId);
                        view.getTableModel().fireTableRowsUpdated(row, row);
                    } catch (ContractorUnavailableException | RecordNotFoundException ex) {
                        showWarning(ex);
                        try {
                            updateTable(new String[1]);
                        } catch (RemoteException ex1) {
                            showError(ex1);
                        }
                    } catch (RemoteException ex) {
                        showError(ex);
                    }
                } else {
                    MsgBox.showWarning("Please enter a Customer ID that consists of 8 digits only.");
                }
            }
        }
    }

    /**
     * Class to handle when the user clicks the Un-Book button on the Client UI.
     */
    private class UnbookListener implements ActionListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = view.getTable().getSelectedRow();
            Contractor contractor = view.getTableModel().getContractor(row);
            try {
                model.unbookContractor(contractor);
                view.getTableModel().fireTableRowsUpdated(row, row);
            } catch (RemoteException ex) {
                showError(ex);
            } catch (RecordNotFoundException ex) {
                showWarning(ex);
                try {
                    updateTable(new String[1]);
                } catch (RemoteException ex1) {
                    showError(ex1);
                }
            }
        }
    }

    /**
     * Class to handle when the user clicks on a Contractor in the {@link javax.swing.JTable} on the Client UI.
     */
    private class TableSelectionListener implements ListSelectionListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            setButtonStates();
        }
    }

    /**
     * Class to handle when the {@link suncertify.ui.ContractorModel} updates.
     */
    private class TableModelUpdateListener implements TableModelListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void tableChanged(TableModelEvent e) {
            setButtonStates();
        }
    }

    /**
     * Sets the Book/Un-book button states, depending on the state of the currently selected Contractor on the UI.
     */
    private void setButtonStates() {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow != -1) {
            String customerId = (String) view.getTable().getValueAt(selectedRow, 5);
            if (customerId == null || customerId.equals("")) {
                view.enableBookingButton(true);
                view.enableUnBookingButton(false);
            } else {
                view.enableBookingButton(false);
                view.enableUnBookingButton(true);
            }
        } else {
            view.enableBookingButton(false);
            view.enableUnBookingButton(false);
        }
    }
}