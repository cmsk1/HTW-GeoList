package com.htwberlin.geolist.net.p2p.wifi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.htwberlin.geolist.Utils;
import com.htwberlin.geolist.net.p2p.ConnectionState;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.p2p.IChannel;
import com.htwberlin.geolist.net.p2p.tcp.TcpMasterChannel;
import com.htwberlin.geolist.net.p2p.tcp.TcpSlaveChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class WifiDirectBroadcastController extends BroadcastReceiver implements WifiP2pManager.PeerListListener {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final WifiP2pManager wifiManager;
    private final WifiP2pManager.Channel globalChannel;
    private final IControllerListener listener;
    private final List<Device> devices = new ArrayList<>();
    private final int port;
    private ScheduledFuture<?> discoverTask;
    private WifiDirectDevice device;

    public WifiDirectBroadcastController(WifiP2pManager wifiManager, WifiP2pManager.Channel globalChannel, IControllerListener listener, int port) {
        this.wifiManager = wifiManager;
        this.globalChannel = globalChannel;
        this.listener = listener;
        this.port = port;
    }

    @SuppressLint("MissingPermission")
    public void connect(WifiDirectDevice device) {
        WifiP2pDevice wifiDevice = device.getNativeDevice();
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiDevice.deviceAddress;

        IFailListener failListener = (reason) -> {
            this.listener.onControllerChanged(ConnectionState.UNABLE_TO_CONNECT);
            System.err.println("unable to connect to device via wifi manager");
        };
        this.wifiManager.connect(this.globalChannel, config, failListener);
    }

    @SuppressLint("MissingPermission")
    public void startDiscovery() {
        this.discoverTask = this.executor.scheduleAtFixedRate(() -> {
            this.wifiManager.discoverPeers(this.globalChannel, null);
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void stopDiscovery() {
        if (this.discoverTask != null) {
            this.discoverTask.cancel(true);
            this.discoverTask = null;
        }
        this.wifiManager.stopPeerDiscovery(this.globalChannel, null);
    }

    public List<Device> getDevices() {
        synchronized (this.devices) {
            return this.devices;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        switch (action) {
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                WifiP2pDevice wifiDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                this.device = new WifiDirectDevice(wifiDevice);
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                this.wifiManager.requestPeers(this.globalChannel, this);
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                this.onConnectionChanged(intent);
                break;
        }
    }

    private void onConnectionChanged(Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

        if (networkInfo == null) {
            this.listener.onControllerChanged(ConnectionState.UNABLE_TO_CONNECT);
        } else if (networkInfo.isConnected()) {
            this.wifiManager.requestConnectionInfo(this.globalChannel, this::onConnectionInfo);
        } else {
            this.listener.onControllerChanged(ConnectionState.DISCONNECTED);
        }
    }

    private void onConnectionInfo(WifiP2pInfo connection) {
        IChannel channel;

        if (connection.isGroupOwner) {
            channel = new TcpMasterChannel(this.device, this.port);
        } else {
            Utils.sleepSafe(2000);
            channel = new TcpSlaveChannel(this.device, connection.groupOwnerAddress, port);
        }
        this.listener.onControllerConnected(channel);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        synchronized (this.devices) {
            this.devices.clear();

            for (WifiP2pDevice device : peers.getDeviceList()) {
                this.devices.add(new WifiDirectDevice(device));
            }
            this.listener.onControllerPeersChanged(this.devices);
        }
    }
}
