package com.basics.paxos;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

abstract class HeroActor extends AbstractActor{
    private final LoggingAdapter log;
    private final String messagePrefix;

    public HeroActor(long id) {
        this.messagePrefix = getClass().getSimpleName()+ " " + id + ": ";
        this.log = Logging.getLogger(getContext().getSystem(), this);
    }

    void Log(String message){
//        log.info(messagePrefix + message);
        System.out.println(messagePrefix + message);
    }
}