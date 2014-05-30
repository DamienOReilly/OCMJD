package suncertify.ui;

import suncertify.common.Contractor;

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
                //TODO show error
            }
        }
    }

    class BookListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    class UnbookListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }



}
