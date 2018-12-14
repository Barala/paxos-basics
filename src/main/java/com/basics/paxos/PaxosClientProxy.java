package com.basics.paxos;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Responsible to talk with proposers
 * 
 * @author barala
 *
 */
public class PaxosClientProxy extends AbstractActor{

    private final ActorRef[] proposers;
    private ActorRef client;
    private long proposalNo;

    public PaxosClientProxy(ActorRef[] proposers) {
        this.proposers = proposers;
    }

    static Props props(ActorRef[] proposers){
        return Props.create(PaxosClientProxy.class, () -> new PaxosClientProxy(proposers));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.ClientRequest.class, (request) -> {
                    client = sender();
                    for(ActorRef proposer : proposers){
                        proposer.tell(request, self());
                    }
                })
                .match(Messages.Response.class, (response) -> {
                    if(response.getProposalNo() > proposalNo){
                        client.tell(response, self());
                        proposalNo = response.getProposalNo();
                    }
                })
                .build();
    }
}