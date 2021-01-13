package com.htwberlin.geolist.logic;

import android.content.Context;

import com.htwberlin.geolist.data.NullDataStorage;
import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.net.NullGeoListProtocolEngine;
import com.htwberlin.geolist.net.NullNetwork;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.p2p.INetwork;
import com.htwberlin.geolist.net.protocol.GeoListProtocolEngine;

public class NullGeoListLogic extends GeoListLogic {
    public static final Device NET_DEVICE = new Device("NullDevice", "75982652");
    private boolean isSharePermitted;

    protected NullGeoListLogic(Context context) {
        super(context);
    }

    public void setSharePermitted(boolean permitted) {
        this.isSharePermitted = permitted;
    }

    public static NullDataStorage getStorage() {
        return (NullDataStorage)GeoListLogic.getStorage();
    }

    @Override
    protected DataStorage createStorage() {
        return new NullDataStorage();
    }

    @Override
    protected INetwork createNetwork() {
        return new NullNetwork(NET_DEVICE);
    }

    @Override
    protected GeoListProtocolEngine createProtocolEngine() {
        return new NullGeoListProtocolEngine(this.storage);
    }

    @Override
    public boolean requestSharePermission() {
        return this.isSharePermitted;
    }

    public static NullGeoListLogic instance() throws IllegalStateException {
        return (NullGeoListLogic)GeoListLogic.instance();
    }

    public static synchronized void makeInstance(Context context) {
        if (instance == null) {
            instance = new NullGeoListLogic(context);
        }
    }
}
