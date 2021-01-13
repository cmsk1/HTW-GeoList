package com.htwberlin.geolist.net.protocol;

import com.htwberlin.geolist.net.p2p.IChannel;
import com.htwberlin.geolist.net.p2p.Device;

import java.io.IOException;

public interface IProtocol {
    void handleConnection(IChannel channel) throws IOException;
}
