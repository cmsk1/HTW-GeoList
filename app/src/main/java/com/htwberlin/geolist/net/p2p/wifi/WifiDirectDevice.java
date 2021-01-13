package com.htwberlin.geolist.net.p2p.wifi;

import android.net.wifi.p2p.WifiP2pDevice;

import com.htwberlin.geolist.net.p2p.Device;

public class WifiDirectDevice extends Device {
    private final WifiP2pDevice wifiDevice;

    public WifiDirectDevice(WifiP2pDevice wifiDevice) {
        super(wifiDevice.deviceAddress, wifiDevice.deviceName);
        this.wifiDevice = wifiDevice;
    }

    public WifiP2pDevice getNativeDevice() {
        return this.wifiDevice;
    }
}
