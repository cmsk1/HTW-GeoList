package com.htwberlin.geolist.net;

import com.htwberlin.geolist.net.p2p.Device;

import java.util.Collection;

@FunctionalInterface
public interface IPeersListener {
    void onPeersDiscovered(Collection<Device> devices);
}
