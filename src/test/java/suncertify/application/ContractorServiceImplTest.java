package suncertify.application;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import suncertify.BaseTest;
import suncertify.db.RecordNotFoundException;

import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Damien O'Reilly
 */
@Ignore
public class ContractorServiceImplTest extends BaseTest {

    private ContractorService service;

    @Before
    public void testSetUp() {
        service = new ContractorServiceImplTestHelper(COPY_DATABASE);
    }

    @Test
    public void testFind() throws RemoteException {
        List<Contractor> contractors = service.search(new String[]{"Dogs With Tools", null, null, null, null, null});

        for (Contractor contractor : contractors) {
            System.out.println(contractor.toString());
        }
    }

    @Test
    public void bookTest() throws RemoteException, RecordNotFoundException, suncertify.db.SecurityException,
            ContractorUnavailableException {
        List<Contractor> contractors = service.search(new String[6]);
        System.out.println(contractors.get(5).toString());
        Contractor contractor = contractors.get(5);
        contractor.setOwner("12345678");
        service.bookContractor(contractor);
        System.out.println(contractors.get(5).toString());
    }

    @After
    public void testTearDown() {
        ((ContractorServiceImplTestHelper) service).close();
    }
}
