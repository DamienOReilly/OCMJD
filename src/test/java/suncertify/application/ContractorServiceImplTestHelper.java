package suncertify.application;

/**
 * @author Damien O'Reilly
 */
public class ContractorServiceImplTestHelper extends ContractorServiceImpl {

    public ContractorServiceImplTestHelper(String databasePath) {
        super(databasePath);
    }

    public void close() {
//        ((Data) database).close();
    }
}
