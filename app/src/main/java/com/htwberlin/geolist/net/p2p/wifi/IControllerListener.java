package com.htwberlin.geolist.net.p2p.wifi;

import com.htwberlin.geolist.net.p2p.ConnectionState;
import com.htwberlin.geolist.net.p2p.IChannel;
import com.htwberlin.geolist.net.p2p.Device;

import java.util.Collection;

public interface IControllerListener {
    void onControllerConnected(IChannel channel);
    void onControllerChanged(ConnectionState state);
    void onControllerPeersChanged(Collection<Device> devices);
}
