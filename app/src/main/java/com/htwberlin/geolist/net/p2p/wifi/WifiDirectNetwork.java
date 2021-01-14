package com.htwberlin.geolist.net.p2p.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;

import com.htwberlin.geolist.net.p2p.AbstractNetwork;
import com.htwberlin.geolist.net.p2p.ConnectionState;
import com.htwberlin.geolist.net.p2p.IChannel;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.p2p.INetwork;

import java.util.Collection;

public class WifiDirectNetwork extends AbstractNetwork implements INetwork, IControllerListener {
    private static final IntentFilter intents = createIntent();
    private final Context context;
    private final WifiP2pManager wifiManager;
    private final WifiP2pManager.Channel globalChannel;
    private final WifiDirectBroadcastController receiver;
    private ConnectionState state = ConnectionState.DISCONNECTED;
    private IChannel channel;

    public WifiDirectNetwork(Context context, int port) {
        this.context = context;
        this.wifiManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.globalChannel = this.wifiManager.initialize(context, Looper.getMainLooper(), null);
        this.receiver = new WifiDirectBroadcastController(this.wifiManager, this.globalChannel, this, port);
    }

    private static IntentFilter createIntent() {
        IntentFilter intents = new IntentFilter();
        intents.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intents.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intents.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        return intents;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startDiscovery() {
        this.context.registerReceiver(this.receiver, intents);
        this.receiver.startDiscovery();
    }

    @Override
    public void stopDiscovery() {
        try {
            this.receiver.stopDiscovery();
            this.context.unregisterReceiver(this.receiver);
        } catch (IllegalArgumentException e) {
            // android bist du dumm?
        }
    }

    @Override
    public void connect(Device device) {
        if (!(device instanceof WifiDirectDevice)) return;
        if (this.state.isActiveState()) return;
        this.state = ConnectionState.CONNECTING;
        this.receiver.connect((WifiDirectDevice) device);
    }

    @Override
    public void disconnect() {
        if (this.channel != null) {
            this.channel.disconnect();
        }
    }

    @Override
    public Collection<Device> getDevices() {
        return this.receiver.getDevices();
    }

    @Override
    public ConnectionState getState() {
        return this.state;
    }

    @Override
    public void onControllerConnected(IChannel channel) {
        this.channel = channel;
        channel.addListener(this::onChannelChanged);
        Runnable run = () -> this.openChannel(channel);
        new Thread(run).start();
    }

    @Override
    public void onControllerChanged(ConnectionState state) {
        if (this.state.isFailure()) {
            this.state = state;
            this.fireStateChange();
        }
    }

    @SuppressLint("MissingPermission")
    private void onChannelChanged(ConnectionState state) {
        this.state = state;
        this.fireStateChange();

        if (!this.state.isActiveState()) {
            this.wifiManager.removeGroup(this.globalChannel, null);
        }
    }

    @Override
    public void onControllerPeersChanged(Collection<Device> devices) {
        this.firePeersDiscovered(devices);
    }
}
