package com.basics.paxos;

import java.util.Objects;

import akka.actor.ActorRef;

public class Messages {

    static class Propose{
        private final long proposalNo;

        public Propose(long proposalNumber) {
            this.proposalNo = proposalNumber;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || !(obj instanceof Propose)){
                return false;
            }

            if(this==obj) return true;
            Propose that = (Propose) obj;

            return proposalNo == that.proposalNo;
        }

        @Override
        public int hashCode() {
            return Objects.hash(proposalNo);
        };

        public long getProposalNo(){
            return this.proposalNo;
        }
    }

    /**
     * 
     * Client will use to make request
     */
    static class ClientRequest{
        private final long value;

        public ClientRequest(long value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || !(obj instanceof ClientRequest)){
                return false;
            }

            if(this==obj) return true;
            ClientRequest that = (ClientRequest) obj;

            return this.value == that.value;
        }

        public long getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        };

        @Override
        public String toString() {
            return "Request{" +
                    "value=" + value +
                    '}';
        }
    }

    static class Response{
        private final long proposalNo;
        private final long learnedValue;

        public long getProposalNo() {
            return proposalNo;
        }

        public long getLearnedValue() {
            return learnedValue;
        }

        public Response(long proposalNo, long learnedValue) {
            this.proposalNo = proposalNo;
            this.learnedValue = learnedValue;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Response)) return false;
            if (this == o) return true;
            Response response = (Response) o;
            return proposalNo == response.proposalNo &&
                    learnedValue == response.learnedValue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(proposalNo, learnedValue);
        }
    }

    /**
     * 
     * 
     * establishing consensus with the acceptors
     *
     */
    static class Promise{
        private final long proposalNo;
        private final long previousProposalNo;
        private final long previousValue;

        public long getProposalNo() {
            return proposalNo;
        }

        public long getPreviousProposalNo() {
            return previousProposalNo;
        }

        public long getPreviousValue() {
            return previousValue;
        }

        public Promise(long proposalNo, long previousProposalNo, long previousValue) {
            this.proposalNo = proposalNo;
            this.previousProposalNo = previousProposalNo;
            this.previousValue = previousValue;
        }

        @Override
        public String toString() {
            return "Promise{" +
                    "proposalNo=" + proposalNo +
                    ", previousProposalNo=" + previousProposalNo +
                    ", previousValue=" + previousValue +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Promise)) return false;
            if (this == o) return true;
            Promise promise = (Promise) o;
            return proposalNo == promise.proposalNo &&
                    previousProposalNo == promise.previousProposalNo &&
                    previousValue == promise.previousValue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(proposalNo, previousProposalNo, previousValue);
        }
    }

    static class Accept{
        private final long proposalNo;
        private final long value;
        private final ActorRef client;

        public Accept(long proposalNo, long value, ActorRef client){
            this.proposalNo = proposalNo;
            this.value = value;
            this.client = client;
        }

        public long getProposalNo() {
            return proposalNo;
        }

        public long getValue() {
            return value;
        }

        public ActorRef getProposer() {
            return client;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Accept)) return false;
            if (this == o) return true;
            Accept accept = (Accept) o;
            return proposalNo == accept.proposalNo &&
                    value == accept.value &&
                    Objects.equals(client, accept.client);
        }

        @Override
        public int hashCode() {
            return Objects.hash(proposalNo, value, client);
        }
    }

    static class Accepted extends Accept {
        public Accepted(long proposalNo, long value, ActorRef proposer){
            super(proposalNo, value, proposer);
        }
    }
}