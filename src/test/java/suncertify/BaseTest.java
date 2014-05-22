package suncertify;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Damien O'Reilly
 */
public class BaseTest {

    protected static final String ORIGINAL_DATABASE = "db-2x2.db";
    protected static final String COPY_DATABASE = "db-2x2.db-test";

    @Before
    public void setUp() {
        try {
            Files.copy(Paths.get(ORIGINAL_DATABASE), Paths.get(COPY_DATABASE), StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            Files.deleteIfExists(Paths.get(COPY_DATABASE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
