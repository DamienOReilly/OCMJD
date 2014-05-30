package suncertify.ui;

import suncertify.common.Contractor;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Damien O'Reilly
 */
public class ContractorModel extends AbstractTableModel {

    private List<String> columnNames = new ArrayList<>();

    private List<Contractor> contractors = new ArrayList<>();

    public ContractorModel() {
        columnNames.add("Name");
        columnNames.add("Location");
        columnNames.add("Specialties");
        columnNames.add("Size");
        columnNames.add("Rate");
        columnNames.add("Customer ID");
    }

    public void updateData(List<Contractor> contractorList) {
        for (Contractor contractor : contractorList) {
            contractors.add(contractor);
        }
        fireTableDataChanged();
    }

    public void clearData() {
        contractors.clear();
        fireTableDataChanged();
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
        return contractors.size();
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if ((rowIndex >= 0) && (rowIndex < contractors.size())) {
            return contractors.get(rowIndex).asArray()[columnIndex];
        }
        return null;
    }

    /**
     * Returns a default name for the column.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }


}
