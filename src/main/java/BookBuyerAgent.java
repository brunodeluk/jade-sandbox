import behaviour.RequestPerformer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class BookBuyerAgent extends Agent {

    private String targetBookTitle;
    private AID[] sellerAgents;

    @Override
    protected void setup() {
        System.out.println("Hello, World! Agent " + getAID().getName() + " is set up!");

        // get the title of the boot as a start-up argument
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetBookTitle = (String) args[0];
            addBehaviour(new TickerBehaviour(this, 15000) {
                @Override
                protected void onTick() {
                    // Search for book seller agents
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("book-selling");
                    template.addServices(sd);

                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        sellerAgents = new AID[result.length];
                        for (int i = 0; i < result.length; i++) {
                            sellerAgents[i] = result[i].getName();
                        }
                    }
                    catch (FIPAException fe) {
                        fe.printStackTrace();
                    }

                    myAgent.addBehaviour(new RequestPerformer(sellerAgents, targetBookTitle));
                }
            });
        }
        else {
            System.out.println("No book title specified");
            // terminate the current agent
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Buyer-agent " + getAID().getName() + " terminating.");
    }
}
