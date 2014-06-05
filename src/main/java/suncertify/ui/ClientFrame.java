package suncertify.ui;

import suncertify.application.Contractor;
import suncertify.utils.Constants;
import suncertify.utils.MsgBox;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class builds the Client UI (view) with interactive components.
 *
 * @author Damien O'Reilly
 */
public class ClientFrame {

    /**
     * JTable that will hold the list of Contractors.
     */
    private final JTable table;

    /**
     * Search Panel UI elements.
     */
    private final JLabel nameLabel = new JLabel("Name:");
    private final JTextField nameField = new JTextField(10);
    private final JLabel locationLabel = new JLabel("Location:");
    private final JTextField locationField = new JTextField(10);
    private final JButton searchButton = new JButton("Search");
    private final JButton refreshButton = new JButton("Refresh");

    /**
     * Booking Panel UI elements.
     */
    private final JButton bookButton = new JButton("Book");
    private final JButton unbookButton = new JButton("Un-book");

    /**
     * Logger instance.
     */
    private static final Logger logger = Logger.getLogger("suncertify.ui");

    /**
     * Instance of the model.
     */
    private final ClientModel model;

    /**
     * Instance of a {@link JTable} model.
     */
    private final ContractorModel contractorModel;

    /**
     * Constructor to initialise the view.
     *
     * @param model Model.
     */
    public ClientFrame(ClientModel model) {
        this.model = model;
        contractorModel = new ContractorModel();
        table = new JTable(contractorModel);
    }

    /**
     * Setup the GUI adding components.
     */
    public void init() {
        JFrame clientFrame = new JFrame();
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
            contractors = model.search(new String[]{});
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            MsgBox.showErrorAndExit(e.getMessage());
        }
        clientFrame.setVisible(true);
        contractorModel.updateData(contractors);
    }

    /**
     * Builds the search panel.
     *
     * @return Search panel.
     */
    private JPanel searchPanel() {
        JPanel panel = new JPanel();
        searchButton.setMnemonic(KeyEvent.VK_S);
        searchButton.setToolTipText("Search the database based on the input criteria.");
        nameField.setToolTipText("Exact name to search for.");
        refreshButton.setMnemonic(KeyEvent.VK_R);
        refreshButton.setToolTipText("Retrieve up to date records from database.");

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

    /**
     * Builds the table panel.
     *
     * @return Table panel.
     */
    private JPanel tablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 1, 5));
        table.setFillsViewportHeight(true);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setDragEnabled(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Builds the footer panel.
     *
     * @return Footer panel.
     */
    private JPanel footerPanelPanel() {
        GridLayout layout = new GridLayout(1, 2);
        JPanel footerPanel = new JPanel(layout);
        footerPanel.add(bookingPanel());
        footerPanel.add(exitButton());
        return footerPanel;
    }

    /**
     * Builds the Booking panel.
     *
     * @return Booking panel.
     */
    private JPanel bookingPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bookButton.setMnemonic(KeyEvent.VK_B);
        bookButton.setToolTipText("Book the selected record.");
        unbookButton.setMnemonic(KeyEvent.VK_U);
        unbookButton.setToolTipText("Un-book the selected record");
        panel.add(bookButton);
        panel.add(unbookButton);
        return panel;
    }

    /**
     * Sets up the exit button and action listener.
     *
     * @return Exit button.
     */
    private Component exitButton() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exitButton = new JButton("Exit");
        exitButton.setToolTipText("Exit the application.");
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

    /**
     * Retrieves the Contractor table model.
     *
     * @return Table model.
     */
    public ContractorModel getTableModel() {
        return contractorModel;
    }

    /**
     * Add the search action listener to the UI components.
     *
     * @param searchListener Search action listener.
     */
    public void addSearchListener(ActionListener searchListener) {
        searchButton.addActionListener(searchListener);
        nameField.addActionListener(searchListener);
        locationField.addActionListener(searchListener);
    }

    /**
     * Add the refresh action listener to the refresh button.
     *
     * @param refreshListener Refresh action listener.
     */
    public void addRefreshListener(ActionListener refreshListener) {
        refreshButton.addActionListener(refreshListener);
    }

    /**
     * Add the book action listener to the book button.
     *
     * @param bookListener Book action listener.
     */
    public void addBookListener(ActionListener bookListener) {
        bookButton.addActionListener(bookListener);
    }

    /**
     * Add the Un-book action listener to the un-book button.
     *
     * @param unbookListener Un-book action listener.
     */
    public void addUnbookListener(ActionListener unbookListener) {
        unbookButton.addActionListener(unbookListener);
    }

    /**
     * Gets the text in name search field.
     */
    public String getNameSearchText() {
        return nameField.getText();
    }

    /**
     * Gets the text in location search field.
     */
    public String getLocationSearchText() {
        return locationField.getText();
    }

    /**
     * Sets the specified text in the name search field.
     *
     * @param input Search action listener.
     */
    public void setNameSearchText(String input) {
        nameField.setText(input);
    }

    /**
     * Sets the specified text in the name search field.
     *
     * @param input Search action listener.
     */
    public void setLocationSearchText(String input) {
        locationField.setText(input);
    }

    /**
     * Gets the JTable instance from the view.
     *
     * @return Table that displays contractors.
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Enables/disables the booking button.
     *
     * @param toggle True/false toggle.
     */
    public void enableBookingButton(boolean toggle) {
        bookButton.setEnabled(toggle);
    }

    /**
     * Enables/disables the un-booking button.
     *
     * @param toggle True/false toggle.
     */
    public void enableUnBookingButton(boolean toggle) {
        unbookButton.setEnabled(toggle);
    }
}