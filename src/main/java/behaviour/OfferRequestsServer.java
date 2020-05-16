package behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Hashtable;

public class OfferRequestsServer extends CyclicBehaviour {

    private Hashtable<String, Integer> catalogue;

    public OfferRequestsServer(Hashtable<String, Integer> catalogue) {
        this.catalogue = catalogue;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        ACLMessage msg = myAgent.receive(template);
        if (msg != null) {
            String bookTitle = msg.getContent();

            ACLMessage msgReply = msg.createReply();

            Integer price = catalogue.get(bookTitle);

            if (price != null) {
                System.out.println("I can sell you " + bookTitle + " for $" + price + "!");
                msgReply.setPerformative(ACLMessage.PROPOSE);
                msgReply.setContent(String.valueOf(price.intValue()));
            }
            else {
                System.out.println("Hmm I don't have that book :/");
                msgReply.setPerformative(ACLMessage.REFUSE);
                msgReply.setContent("not-available");
            }

            myAgent.send(msgReply);
        }
        else {
            // in order to avoid CPU Consumption, we want to execute
            // the action() method behaviour only when a new message is
            // received. In order to do that we can use the block() method of
            // the Behaviour.java class. This method marks the behaviour as "blocked"
            // so that the agent does not schedule it for execution anymore.
            // When a new message is inserted into the queue, all blocked behaviours
            // become available for execution.
            block();
        }
    }

}
