package suncertify.db.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Damien O'Reilly
 */
public class DataBaseIO {
    private String database;

    public DataBaseIO(String database) {
        this.database = database;
    }

    public void printMagic() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(database, "rw")) {
            System.out.println("Magic: " + file.readInt());
            System.out.println("Offset of first record: " + file.readInt());
        }
    }


    //TODO: remove, this is for test only.
    public static void main(String[] args) {
        DataBaseIO dataBaseIO = new DataBaseIO("db-2x2.db");
        try {
            dataBaseIO.printMagic();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
