package com.htwberlin.geolist.net.p2p.wifi;

import android.net.wifi.p2p.WifiP2pDevice;

import com.htwberlin.geolist.net.p2p.Device;

import java.util.Objects;

public class WifiDirectDevice extends Device {
    private final WifiP2pDevice wifiDevice;

    //Note: Hash of name is used for signature because newer android devices generate random mac addresses
    public WifiDirectDevice(WifiP2pDevice wifiDevice) {
        super(String.valueOf(Objects.hash(wifiDevice.deviceName)), wifiDevice.deviceName);
        this.wifiDevice = wifiDevice;
    }

    public WifiP2pDevice getNativeDevice() {
        return this.wifiDevice;
    }
}
