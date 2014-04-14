package suncertify.utils;

import suncertify.Constants;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is a singleton and deals with persisting properties that is used by this application.
 *
 * @author Damien O'Reilly
 */
public class PropertiesManager {
    /**
     * The name of the file that wil store properties.
     */
    private static final String PROPERTIES_FILE = "suncertify.properties";

    /**
     * Lock to synchronize access to loading/saving of properties. It is possible for multiple clients to try save
     * properties at the same time.
     */
    private static Lock propertiesLock = new ReentrantLock();

    /**
     * Constant representing the path to the database
     */
    private static final String DATABASE_PATH = "dbpath";

    /**
     * Constant representing the RMI host name
     */
    private static final String RMI_HOST = "rmihost";

    /**
     * Constant representing the RMI port
     */
    private static final String RMI_PORT = "rmiport";

    /**
     * Singleton instance.
     */
    private static PropertiesManager propertiesManager = new PropertiesManager();

    /**
     *
     */
    private static Properties properties;

    /**
     * Default constructor. Private due to use of singleton pattern. Loads existing properties, if any.
     */
    private PropertiesManager() {
        load();
        if (properties == null) {
            properties = new Properties();
        }
    }

    /**
     * Returns the instance of this class.
     *
     * @return The instance of this class.
     */
    public static PropertiesManager getInstance() {
        return propertiesManager;
    }

    /**
     * Load the persisted properties from disk.
     */
    private void load() {
        propertiesLock.lock();
        try (InputStream in = new BufferedInputStream(new FileInputStream(PROPERTIES_FILE))) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            propertiesLock.unlock();
        }
    }

    /**
     * Save the properties to disk.
     */
    private void save() {
        propertiesLock.lock();
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(PROPERTIES_FILE))) {
            properties.store(out, Constants.APPLICATION_NAME + " configuration.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            propertiesLock.unlock();
        }
    }

    /**
     * Sets a property.
     *
     * @param key   The key of the property.
     * @param value The associated value of the property.
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        save();
    }

    /**
     * Gets a property based on its key.
     *
     * @param key The key to retrieve a property.
     * @return The value for the given key.
     */
    public String getProperty(String key) {
        return getProperty(key);
    }

    /**
     * Gets a property based on its key. If property is not found, the submitted default value is returned.
     *
     * @param key          The key to retrieve a property.
     * @param defaultValue The default value to return if the property doesn't exist.
     * @return They value for the given key.
     */
    public String getProperty(String key, String defaultValue) {

        String value = properties.getProperty(key);
        if (value == null) {
            value = defaultValue;
            properties.setProperty(key, defaultValue);
        }
        return value;
    }
}