package com.htwberlin.geolist.net;

import com.htwberlin.geolist.net.p2p.AbstractNetwork;
import com.htwberlin.geolist.net.p2p.ConnectionState;
import com.htwberlin.geolist.net.p2p.IChannel;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.p2p.INetwork;
import com.htwberlin.geolist.net.p2p.tcp.TcpMasterChannel;
import com.htwberlin.geolist.net.p2p.tcp.TcpSlaveChannel;
import com.htwberlin.geolist.net.protocol.GeoListProtocolEngine;
import com.htwberlin.geolist.utils.AssertThread;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NullNetwork extends AbstractNetwork implements INetwork {
    private static final Map<String, NullNetwork> deviceNetworks = new HashMap<>();

    private ConnectionState state = ConnectionState.DISCONNECTED;
    private final List<Device> devices = new ArrayList<>();
    private final Device ownDevice;
    private IChannel channel;
    private boolean inDiscovery;
    private AssertThread thread;

    public NullNetwork(Device ownDevice) {
        this.ownDevice = ownDevice;
        deviceNetworks.put(ownDevice.getSignature(), this);
    }

    @Override
    public void startDiscovery() {
        this.inDiscovery = true;
    }

    @Override
    public void stopDiscovery() {
        this.inDiscovery = false;
    }

    @Override
    public void connect(Device device) {
        if (this.state.isActiveState()) return;
        String signature = device.getSignature();
        NullNetwork network = deviceNetworks.get(signature);

        if (network == null) {
            this.state = ConnectionState.UNABLE_TO_CONNECT;
            return;
        }
        this.state = ConnectionState.CONNECTING;
        TcpSlaveChannel slave = new TcpSlaveChannel(network.ownDevice, InetAddress.getLoopbackAddress(), GeoListProtocolEngine.PORT);
        TcpMasterChannel master = new TcpMasterChannel(this.ownDevice, GeoListProtocolEngine.PORT);
        network.openConnection(slave);
        this.openConnection(master);
    }

    private void openConnection(IChannel channel) {
        this.channel = channel;
        channel.addListener((state) -> this.state = state);
        AssertThread.AssertRunnable run = () -> this.openChannel(channel);
        this.thread = new AssertThread(run);
        this.thread.start();
    }

    @Override
    public void disconnect() {
        if (this.state != ConnectionState.CONNECTED) return;
        this.channel.disconnect();
    }

    @Override
    public Collection<Device> getDevices() {
        if (this.inDiscovery) {
            return this.devices;
        }
        return new ArrayList<>();
    }

    @Override
    public ConnectionState getState() {
        return this.state;
    }

    public void testChannelThread() {
        if (this.thread != null) {
            this.thread.test();
            this.thread = null;
        }
    }
}
