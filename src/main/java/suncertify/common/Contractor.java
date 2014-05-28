package suncertify.common;

import java.io.Serializable;

/**
 * Java Bean like class that is used in the the business logic between client and server.
 *
 * @author Damien O'Reilly
 */
public class Contractor implements Serializable {

    private int recordId;
    private String name;
    private String location;
    private String specialties;
    private String size;
    private String rate;
    private String owner;

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

    public Contractor(final int recordId, final String[] record) {
        this(recordId, record[0], record[1], record[2], record[3], record[4], record[5]);
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String[] asArray() {
        return new String[]{this.name, this.location, this.specialties, this.size, this.rate, this.owner};
    }

    @Override
    public String toString() {
        return "Record ID=[" + recordId + "], Name=[" + name + "], Location=[" + location + "], " +
                "Specialties=[" + specialties + "], Size=[" + size + "], Rate=[" + rate + "], Owner=[" + owner + "]";
    }
}
