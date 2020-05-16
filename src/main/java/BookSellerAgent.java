import behaviour.OfferRequestsServer;
import behaviour.PurchaseRequestsServer;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Hashtable;
import java.util.Random;

public class BookSellerAgent extends Agent {

    private Hashtable<String, Integer> catalogue;

    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-selling");
        sd.setName("JADE-book-trading");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        this.catalogue = new Hashtable<>();
        catalogue.put("Lord of the rings", new Random().nextInt(100));
        catalogue.put("Harry Potter", new Random().nextInt(100));
        catalogue.put("The da Vinci Code", new Random().nextInt(100));
        catalogue.put("The Dome", new Random().nextInt(100));
        catalogue.put("The Outsider", new Random().nextInt(100));

        addBehaviour(new OfferRequestsServer(catalogue));
        addBehaviour(new PurchaseRequestsServer());

        System.out.println("Book Seller Agent " + getAID().getName() + " is set up!");
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("Seller agent " + getAID().getName() + " terminating.");
    }
}
