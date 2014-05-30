package suncertify.ui;

import suncertify.application.ContractorService;
import suncertify.common.Constants;
import suncertify.common.Contractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Damien O'Reilly
 */
public class ClientFrame {

    private JFrame clientFrame;

    private JTable table;

    // Search Panel UI elements.
    private JLabel nameLabel = new JLabel("Name:");
    private JTextField nameField = new JTextField(10);
    private JLabel locationLabel = new JLabel("Location:");
    private JTextField locationField = new JTextField(10);
    private JButton searchButton = new JButton("Search");
    private JButton refreshButton = new JButton("Refresh");

    //Booking Panel UI elements.
    private JButton bookButton = new JButton("Book");
    private JButton unbookButton = new JButton("Un-book");

    private ContractorService service;

    private ContractorModel contractorModel;

    public ClientFrame(ContractorService service) {
        this.service = service;
    }

    public void init() {
        clientFrame = new JFrame();
        clientFrame.setTitle(Constants.APPLICATION_NAME + " - " + Constants.APPLICATION_VERSION);
        clientFrame.setSize(1000, 700);
        clientFrame.setLocationRelativeTo(null);
        clientFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        clientFrame.setResizable(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(searchPanel());
        panel.add(tablePanel());
        panel.add(footerPanelPanel());

        clientFrame.add(panel);

        List<Contractor> contractors = null;
        try {
            contractors = service.search(new String[]{});
        } catch (RemoteException e) {
            //TODO show error and exit
        }
        clientFrame.setVisible(true);
        contractorModel.updateData(contractors);
    }

    // Search Panel
    private JPanel searchPanel() {
        JPanel panel = new JPanel();
        searchButton.setMnemonic(KeyEvent.VK_S);
        refreshButton.setMnemonic(KeyEvent.VK_R);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(locationLabel);
        panel.add(locationField);
        panel.add(searchButton);
        panel.add(refreshButton);

        panel.setBorder(BorderFactory.createTitledBorder("Search"));
        panel.setMaximumSize(new Dimension(550, 100));
        return panel;
    }

    // Table of Contractors
    private JScrollPane tablePanel() {
        contractorModel = new ContractorModel();
        table = new JTable(contractorModel);
        table.setFillsViewportHeight(true);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setDragEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        return scrollPane;
    }

    // Footer Panel
    private JPanel footerPanelPanel() {
        GridLayout layout = new GridLayout(1, 2);
        JPanel footerPanel = new JPanel(layout);
        footerPanel.add(bookingPanel());
        footerPanel.add(exitButton());
        return footerPanel;
    }

    private JPanel bookingPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bookButton.setMnemonic(KeyEvent.VK_B);
        unbookButton.setMnemonic(KeyEvent.VK_U);
        panel.add(bookButton);
        panel.add(unbookButton);
        return panel;
    }

    private Component exitButton() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exitButton = new JButton("Exit");
        exitButton.setMnemonic(KeyEvent.VK_E);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panel.add(exitButton);
        return panel;
    }

    public ContractorModel getTableModel() {
        return contractorModel;
    }

    public void addSearchListener(ActionListener searchListener) {
        searchButton.addActionListener(searchListener);
    }

    public void addRefreshListener(ActionListener refreshListener) {
        refreshButton.addActionListener(refreshListener);
    }

    public void addBookListener(ActionListener bookListener) {
        bookButton.addActionListener(bookListener);
    }

    public void addUnbookListener(ActionListener unbookListener) {
        unbookButton.addActionListener(unbookListener);
    }

    public String getNameSearchText() {
        return nameField.getText();
    }

    public String getLocationSearchText() {
        return locationField.getText();
    }

    public void setNameSearchText(String input) {
        nameField.setText(input);
    }

    public void setLocationSearchText(String input) {
        locationField.setText(input);
    }
}
