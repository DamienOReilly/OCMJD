package suncertify.db;

/**
 * This class is responsible for creation of instances of {@link DB}.
 *
 * @author Damien O'Reilly
 */
public class DatabaseFactory {

    private static DB database;

    /**
     * Method to return {@link DB} instance for a given database location.
     *
     * @param databasePath Database path.
     * @return {@code DB} instance providing low level operations on the database.
     */
    public static DB getDatabase(String databasePath) {
        if (database == null) {
            database = new Data(databasePath);
        }
        return database;
    }
}
