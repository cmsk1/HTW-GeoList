package com.htwberlin.geolist.net.p2p;

import java.io.IOException;

@FunctionalInterface
public interface INetworkListener {
    void handleConnection(IChannel channel) throws IOException;
}
