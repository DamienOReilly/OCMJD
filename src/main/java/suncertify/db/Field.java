package suncertify.db;

/**
 * This class acts as a POJO which holds record column specific details.
 *
 * @author Damien O'Reilly
 */
public class Field {
    private String name;
    private Short length;

    public Field(String name, Short length) throws IllegalArgumentException {

        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Field name cannot be null or empty string.");
        }

        if (length == null || length < 1) {
            throw new IllegalArgumentException("Field length should be greater than 0.");
        }

        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public Short getLength() {
        return length;
    }

}