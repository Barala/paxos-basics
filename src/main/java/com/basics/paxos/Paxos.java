package com.basics.paxos;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.Future;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;

public class Paxos {
    private final int totalLearners;
    private final int totalAcceptors;
    private final int totalProposers;
    private final Duration timeout;

    private ActorSystem actorSystem;
    private ActorRef client;

    public Paxos(int totalLearners, int totalAcceptors, int totalProposers, Duration timeout) {
        this.totalLearners = totalLearners;
        this.totalAcceptors = totalAcceptors;
        this.totalProposers = totalProposers;
        this.timeout = timeout;
    }

    public void start(){
        actorSystem = actorSystem.create("paxos-basics");

        final ActorRef[] learners = new ActorRef[totalLearners];
        for(int i=0;i<totalLearners;i++){
            learners[i] = actorSystem.actorOf(Learner.props(i), "learner" + i);
        }

        final ActorRef[] acceptors = new ActorRef[totalAcceptors];
        for(int i=0;i<totalAcceptors;i++){
            acceptors[i] = actorSystem.actorOf(Acceptor.props(i, learners), "acceptor" + i);
        }

        final ActorRef[] proposers = new ActorRef[totalProposers];
        for(int i=0;i<totalProposers;i++){
            proposers[i] = actorSystem.actorOf(Proposer.props(i, acceptors, timeout), "proposer"+ i);
        }

        client = actorSystem.actorOf(PaxosClientProxy.props(proposers), "paxos-client-proxy");
    }

    Messages.Response doUpdate(long value) throws Exception {
        Objects.requireNonNull(actorSystem, "Actor system not yet started");
        final long maxTimeout = timeout.toMillis() * 3;
        Future<Object> ask = Patterns.ask(client, new Messages.ClientRequest(value), maxTimeout);
        return (Messages.Response)
                Await.result(ask, scala.concurrent.duration.Duration.create(maxTimeout, TimeUnit.MILLISECONDS));
    }

    void shutdown(){
        actorSystem.terminate();
    }
}