package suncertify.ui;

import suncertify.application.ContractorUnavailableException;
import suncertify.common.Contractor;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Damien O'Reilly
 */
public class ClientController {

    private ClientModel model;
    private ClientFrame view;

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

    class SearchListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String[] criteria = new String[]{view.getNameSearchText().trim(), view.getLocationSearchText().trim()};
            try {
                List<Contractor> contractors = model.search(criteria);
                view.getTableModel().clearData();
                view.getTableModel().updateData(contractors);
            } catch (RemoteException ex) {
                ex.printStackTrace();
                //TODO show error
            }
        }
    }

    class RefreshListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String[] criteria = new String[]{};
            try {
                List<Contractor> contractors = model.search(criteria);
                view.getTableModel().clearData();
                view.setNameSearchText("");
                view.setLocationSearchText("");
                view.getTableModel().updateData(contractors);
            } catch (RemoteException ex) {
                ex.printStackTrace();
                //TODO show error
            }
        }
    }

    class BookListener implements ActionListener {

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
                    } catch (suncertify.db.SecurityException ex) {
                        ex.printStackTrace();
                        //TODO
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                        //TODO
                    } catch (RecordNotFoundException ex) {
                        ex.printStackTrace();
                        //TODO
                    } catch (ContractorUnavailableException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    //TODO enter 8 digits
                }
            }
        }
    }

    class UnbookListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = view.getTable().getSelectedRow();
            Contractor contractor = view.getTableModel().getContractor(row);
            try {
                model.unbookContractor(contractor);
                view.getTableModel().fireTableRowsUpdated(row, row);
            } catch (RemoteException e1) {
                e1.printStackTrace();
                //TODO
            } catch (SecurityException e1) {
                e1.printStackTrace();
                //TODO
            } catch (RecordNotFoundException e1) {
                e1.printStackTrace();
                //TODO
            } catch (ContractorUnavailableException e1) {
                e1.printStackTrace();
            }
        }
    }

    class TableSelectionListener implements ListSelectionListener {

        /**
         * Called whenever the value of the selection changes.
         *
         * @param e the event that characterizes the change.
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            setButtonStates();
        }
    }

    class TableModelUpdateListener implements TableModelListener {

        /**
         * This fine grain notification tells listeners the exact range
         * of cells, rows, or columns that changed.
         *
         * @param e
         */
        @Override
        public void tableChanged(TableModelEvent e) {
            setButtonStates();
        }
    }

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