package com.htwberlin.geolist.net.p2p;

import com.htwberlin.geolist.net.IPeersListener;

import java.util.Collection;

public interface INetwork {
    void startDiscovery();
    void stopDiscovery();
    void connect(Device device);
    void disconnect();
    ConnectionState getState();
    Collection<Device> getDevices();

    void setNetworkListener(INetworkListener listener);
    void setPeersListener(IPeersListener listener);
    void setStateChangeListener(Runnable listener);
}
