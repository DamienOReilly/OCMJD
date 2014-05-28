package suncertify.ui;

/**
 * @author Damien O'Reilly
 */
public class ClientController {

    private ContractorModel model;
    private ClientFrame view;

    public ClientController(ContractorModel model, ClientFrame view) {
        this.model = model;
        this.view = view;
    }

}
