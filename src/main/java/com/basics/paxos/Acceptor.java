package com.basics.paxos;

import akka.actor.ActorRef;
import akka.actor.Props;

public class Acceptor extends HeroActor{
    //each acceptor shall have unique id
    private final long id;
    private final ActorRef[] learners;

    //
    private long highestProposalNo = -1;
    private long previousValue = -1;

    Acceptor(final long id, final ActorRef[] learners) {
        super(id);
        this.id = id;
        this.learners = learners;
    }

    static Props props(long id, ActorRef[] learners){
        return Props.create(Acceptor.class, () -> new Acceptor(id, learners));
    }

    @Override
    public Receive createReceive() {
        //Acceptor will get proposal request
        return receiveBuilder()
                .match(Messages.Propose.class, (propose) -> { 
                    if(propose.getProposalNo()>highestProposalNo){
                        sender().tell(new Messages.Promise(propose.getProposalNo(),
                                highestProposalNo, previousValue), self());
                        highestProposalNo = propose.getProposalNo();
                    }
                })
                .match(Messages.Accept.class, (accept) -> {
                    if(highestProposalNo == accept.getProposalNo()){
                        final Messages.Accepted msg = new Messages.Accepted(accept.getProposalNo(),
                                accept.getValue(), accept.getProposer());
                        sender().tell(msg, self());
                        for(ActorRef learner : learners){
                            learner.tell(msg, self());
                        }
                        previousValue = accept.getValue();
                    }
                })
                .build();
    }

    @Override
    public String toString() {
        return "[Acceptor: " + id + "]";
    };
}