package com.htwberlin.geolist.net.p2p.wifi;

import android.net.wifi.p2p.WifiP2pManager;

@FunctionalInterface
interface IFailListener extends WifiP2pManager.ActionListener {
    @Override
    default void onSuccess() {}
}
