package com.htwberlin.geolist.net.p2p;

@FunctionalInterface
public interface IChannelListener {
    void onStateChanged(ConnectionState state);
}
