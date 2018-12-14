package com.basics.paxos;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;

public class Proposer extends HeroActor{
    private final long id;

    private final ActorRef[] acceptors;
    private final Duration timeout;

    private long value;
    private long proposalNo;

    private List<Messages.Promise> promises;
    private ActorRef client;
    private Cancellable timeoutScheduler;

    public Proposer(long id, ActorRef[] acceptors, Duration timeout) {
        super(id);
        this.id = id;
        this.acceptors = acceptors;
        this.timeout = timeout;
        this.promises = new ArrayList<Messages.Promise>();
    }

    static Props props(long id, ActorRef[] acceptors, Duration timeout){
        return Props.create(Proposer.class, () -> new Proposer(id, acceptors, timeout));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.ClientRequest.class, (request) -> {
                    Log("recieved request "  + request.toString());
                    value = request.getValue();
                    proposalNo++;
                    promises.clear();
                    client = sender();
                    if(timeoutScheduler!=null){
                        timeoutScheduler.cancel();
                    }
                    for(ActorRef acceptor : acceptors){
                        acceptor.tell(new Messages.Propose(proposalNo), self());
                    }
                    timeoutScheduler = context().system().scheduler().scheduleOnce(
                            timeout, self(), request, context().dispatcher(), sender());
                })
                .match(Messages.Promise.class, (promise) -> {
                    Log("received promise :: " + promise.toString());
                    if(promise.getProposalNo() == this.proposalNo){
                        promises.add(promise);
                        acceptIfQuorumOfResponsesAndSend();
                    }
                    //there is possibility that other proposal have successfully proposed something in the system
                    else if(promise.getPreviousProposalNo() > proposalNo){
                        proposalNo = promise.getPreviousProposalNo();
                        value = promise.getPreviousValue();
                    }
                })
                .build();
    }

    private void acceptIfQuorumOfResponsesAndSend(){
        if(promises.size() >= acceptors.length/2 + 1){
            timeoutScheduler.cancel();
            for(ActorRef acceptor : acceptors){
                //send to all acceptors who has replied back
                acceptor.tell(new Messages.Accept(proposalNo, value, client), self());
            }
        }
    }

    @Override
    public String toString() {
        return "[Proposer: "+ id + "]";
    }
}