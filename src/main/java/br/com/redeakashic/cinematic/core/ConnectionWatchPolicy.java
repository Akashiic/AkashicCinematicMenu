package br.com.redeakashic.cinematic.core;
/** Pure lifecycle policy: returning to a menu does not prove channel closure. */
public final class ConnectionWatchPolicy {
    public static final int MAX_PENDING=1;
    public enum Decision { WAIT, CLOSE_CANCELLED, RELEASE }
    private ConnectionWatchPolicy(){}
    public static boolean canAdmit(int pending){
        if(pending<0)throw new IllegalArgumentException("pending");
        return pending<MAX_PENDING;
    }
    public static Decision decide(boolean cancelled,boolean returnedToMenu,boolean managerPresent,
            boolean channelOpen,boolean workerFinished,boolean connectorCurrent,boolean worldPresent){
        if(channelOpen&&!managerPresent)throw new IllegalArgumentException("Channel without manager");
        if(cancelled||returnedToMenu){
            if(channelOpen)return Decision.CLOSE_CANCELLED;
            return workerFinished?Decision.RELEASE:Decision.WAIT;
        }
        if(worldPresent||(!connectorCurrent&&workerFinished))return Decision.RELEASE;
        return Decision.WAIT;
    }
}
