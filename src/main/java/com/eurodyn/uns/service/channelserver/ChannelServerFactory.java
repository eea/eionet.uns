package com.eurodyn.uns.service.channelserver;

public class ChannelServerFactory {

    public static short ACTIVE_SERVER = 1;

    private static BaseChannelServer server;

    static {
        if (1 == ACTIVE_SERVER) {
            server = new EEAChannelServer();
        }
    }

    public static BaseChannelServer getActiveServer() {
        return server;
    }

}
