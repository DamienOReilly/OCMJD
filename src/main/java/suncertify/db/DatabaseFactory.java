package suncertify.db;

/**
 * @author Damien O'Reilly
 */
public class DatabaseFactory {

    private static DB database;

    public static DB getDatabase(String databasePath) {
        if (database == null) {
            database = new Data(databasePath);
        }
        return database;
    }
}
