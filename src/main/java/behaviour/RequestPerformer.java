package behaviour;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RequestPerformer extends Behaviour {

    private AID[] sellerAgents;
    private String targetBookTitle;
    private AID bestSeller; // The agent who provides the best offer
    private int bestPrice; // The best offer price
    private int repliesCnt = 0; // The counter of replies from seller agents
    private MessageTemplate template; // The template to receive replies
    private int step = 0;

    public RequestPerformer(AID[] sellerAgents, String targetBookTitle) {
        this.sellerAgents = sellerAgents;
        this.targetBookTitle = targetBookTitle;
    }

    @Override
    public void action() {
        switch (step) {
            case 0:
                System.out.println("Can someone sell me " + targetBookTitle + "?");
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (AID sellerAgent : sellerAgents) {
                    cfp.addReceiver(sellerAgent);
                }

                cfp.setContent(targetBookTitle);
                cfp.setConversationId("book-trade");
                cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                myAgent.send(cfp);

                template = MessageTemplate.and(
                        MessageTemplate.MatchConversationId("book-trade"),
                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

                step = 1;
                break;
            case 1:
                // Receive al proposals/refusals from seller agents
                ACLMessage reply = myAgent.receive(template);
                if (reply != null) {
                    if (reply.getPerformative() == ACLMessage.PROPOSE) {
                        System.out.println("I've got a proposal from " + reply.getSender().getName());
                        // this is an offer
                        int price = Integer.parseInt(reply.getContent());
                        if (bestSeller == null || price < bestPrice) {
                            bestPrice = price;
                            bestSeller = reply.getSender();
                        }
                    }

                    repliesCnt++;
                    System.out.println(repliesCnt + "/" + sellerAgents.length);
                    if (repliesCnt >= sellerAgents.length) {
                        System.out.println("We received all replies");
                        // We received all replies
                        step = 2;
                    }
                }
                else {
                    block();
                }
                break;
            case 2:
                // Send the purchase order to the seller that provided the best offer
                System.out.println(bestSeller.getName() + " I accept your proposal");
                ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                order.addReceiver(bestSeller);
                order.setContent(targetBookTitle);
                order.setConversationId("book-trade");
                order.setReplyWith("order" + System.currentTimeMillis());
                myAgent.send(order);

                template = MessageTemplate.and(
                        MessageTemplate.MatchConversationId("book-trade"),
                        MessageTemplate.MatchInReplyTo(order.getReplyWith()));

                step = 3;
                break;
            case 3:
                // Receive the purchase order reply
                reply = myAgent.receive(template);
                if (reply != null) {
                    // Purchase order reply received
                    if (reply.getPerformative() == ACLMessage.INFORM) {
                        System.out.println(targetBookTitle + " successfully purchased.");
                        System.out.println("Price = " + bestPrice);
                        myAgent.doDelete();
                    }

                    step = 4;
                }
                else {
                    block();
                }

                break;
        }
    }

    @Override
    public boolean done() {
        return (step == 4 || (step == 2 && bestSeller == null));
    }
}
