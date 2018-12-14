package com.basics.paxos;
import akka.actor.ActorRef;
import akka.actor.Props;


public class Learner extends HeroActor{
    //each learner should have unique id
    private final long id;
    private long lastProposalNo = -1;
    private long lastValue = -1;

    public Learner(long id) {
        super(id);
        this.id = id;
    }

    static Props props(long id){
        return Props.create(Learner.class, () -> new Learner(id));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
               .match(Messages.Accepted.class, (accepted) -> {
                   if(lastProposalNo < accepted.getProposalNo()){
                       lastProposalNo = accepted.getProposalNo();
                       lastValue = accepted.getValue();
                       Log("Received accepted. No: " + accepted.getProposalNo() + " value: " + accepted.getValue());
                       accepted.getProposer().tell(new Messages.Response(lastProposalNo, lastValue), ActorRef.noSender());
                   }
               })
               .build();
    };

    @Override
    public String toString() {
        return "[Learner: "+ id + "]";
    }
}
