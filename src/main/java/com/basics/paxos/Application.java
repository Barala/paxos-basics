package com.basics.paxos;

import java.time.Duration;


public class Application {

    public static void main(String[] args) throws Exception {
        int numProposers = 2;
        int numAcceptors = 4;
        int numLearners = 5;

        Duration timeout = Duration.ofSeconds(5);
        Paxos paxos = new Paxos(numLearners, numAcceptors, numProposers, timeout);
        paxos.start();

        paxos.doUpdate(42);
        paxos.doUpdate(23);
        paxos.doUpdate(5);

        paxos.shutdown();
    }
}
