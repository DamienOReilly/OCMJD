package suncertify.application;

import java.io.Serializable;

/**
 * Java Bean like class that is used in the the business logic between client and server.
 *
 * @author Damien O'Reilly
 */
public class Contractor implements Serializable {

    /**
     * Private fields of a Contractor.
     */
    private int recordId;
    private String name;
    private String location;
    private String specialties;
    private String size;
    private String rate;
    private String owner;

    /**
     * Constructs a {@code Contractor} based on individual submitted info.
     *
     * @param recordId    Record ID
     * @param name        Contractor Name
     * @param location    Location
     * @param specialties Specialties
     * @param size        Number of People.
     * @param rate        Rate of Contractor
     * @param owner       Customer ID
     */
    public Contractor(int recordId, String name, String location, String specialties, String size, String rate,
                      String owner) {
        this.recordId = recordId;
        this.name = name;
        this.location = location;
        this.specialties = specialties;
        this.size = size;
        this.rate = rate;
        this.owner = owner;
    }

    /**
     * Constructs a {@code} Contractor from a {@link String} array.
     *
     * @param recordId Record ID
     * @param record   Array containing record information.
     */
    public Contractor(final int recordId, final String[] record) {
        this(recordId, record[0], record[1], record[2], record[3], record[4], record[5]);
    }

    /**
     * Gets the Record ID.
     *
     * @return Record ID
     */
    public int getRecordId() {
        return recordId;
    }

    /**
     * Sets the Record ID.
     *
     * @param recordId Record ID
     */
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    /**
     * Gets the Contractor name.
     *
     * @return Contractor name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Contractor name.
     *
     * @param name Contractor name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Contractor location.
     *
     * @return Contractor location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the Contractor location.
     *
     * @param location Contractor location.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the Contractor's specialties.
     *
     * @return Contractor specialties.
     */
    public String getSpecialties() {
        return specialties;
    }

    /**
     * Sets the Contractor specialties.
     *
     * @param specialties Contractor specialties.
     */
    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    /**
     * Gets the Contractor size in people.
     *
     * @return Contractor size.
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the Contractor size in people.
     *
     * @param size Contractor size.
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Gets the Contractor rate.
     *
     * @return Contractor rate.
     */
    public String getRate() {
        return rate;
    }

    /**
     * Sets the Contractor rate.
     *
     * @param rate Contractor rate.
     */
    public void setRate(String rate) {
        this.rate = rate;
    }

    /**
     * Gets the Customer ID that has this contractor booked.
     *
     * @return Customer ID.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the Customer ID that has this contractor booked.
     *
     * @param owner Customer ID.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String[] asArray() {
        return new String[]{this.name, this.location, this.specialties, this.size, this.rate, this.owner};
    }

    /**
     * Returns a String representation of the members of this class.
     *
     * @return String readable representation.
     */
    @Override
    public String toString() {
        return "Record ID=[" + recordId + "], Name=[" + name + "], Location=[" + location + "], " +
                "Specialties=[" + specialties + "], Size=[" + size + "], Rate=[" + rate + "], Owner=[" + owner + "]";
    }
}