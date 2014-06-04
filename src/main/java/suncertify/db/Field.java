package suncertify.db;

/**
 * This class acts as a POJO which holds record column specific details.
 *
 * @author Damien O'Reilly
 */
public class Field {

    /**
     * Name of the field.
     */
    private String name;

    /**
     * Length of the field.
     */
    private Short length;

    /**
     * Constructor to create a {@code Field} object.
     *
     * @param name   Name of the field.
     * @param length Length of the field.
     * @throws IllegalArgumentException Invalid field length.
     */
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

    /**
     * Gets the field name.
     *
     * @return Field name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the field length.
     *
     * @return Field length.
     */
    public Short getLength() {
        return length;
    }

}