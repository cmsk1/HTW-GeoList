package com.htwberlin.geolist.logic;

import android.content.Context;

import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.data.interfaces.DataStorageImpl;
import com.htwberlin.geolist.net.p2p.INetwork;
import com.htwberlin.geolist.net.p2p.wifi.WifiDirectNetwork;
import com.htwberlin.geolist.net.protocol.GeoListProtocolEngine;
import com.htwberlin.geolist.net.helper.NetInterface;

public class GeoListLogic {
    protected static GeoListLogic instance;
    protected final Context context;
    protected DataStorage storage;
    protected NetInterface net;

    protected GeoListLogic(Context context) {
        this.context = context;
        this.storage = this.createStorage();

        this.net = this.createNetInterface();
    }

    public static DataStorage getStorage() {
        return instance().storage;
    }

    public static NetInterface getNetInterface() {
        return instance().net;
    }

    public static Context getAppContext() {
        return instance().context;
    }

    public boolean requestSharePermission() {
        return true;
    }

    protected DataStorage createStorage() {
        return new DataStorageImpl(this.context);
    }

    protected INetwork createNetwork() {
        return new WifiDirectNetwork(this.context, GeoListProtocolEngine.PORT);
    }

    protected GeoListProtocolEngine createProtocolEngine() {
        return new GeoListProtocolEngine(this.storage);
    }

    protected NetInterface createNetInterface() {
        GeoListProtocolEngine engine = this.createProtocolEngine();
        INetwork network = this.createNetwork();
        return new NetInterface(network, engine, this.storage);
    }

    public static GeoListLogic instance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("instance was not initialized");
        }
        return instance;
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static synchronized void makeInstance(Context context) {
        if (instance == null) {
            instance = new GeoListLogic(context.getApplicationContext());
        }
    }
}
