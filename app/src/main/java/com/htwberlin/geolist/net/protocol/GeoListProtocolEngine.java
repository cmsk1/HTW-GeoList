package com.htwberlin.geolist.net.protocol;

import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.logic.GeoListLogic;

import java.util.UUID;

public class GeoListProtocolEngine {
    public static final int PORT = 43234;
    protected final DataStorage storage;

    public GeoListProtocolEngine(DataStorage storage) {
        this.storage = storage;
    }

    public GeoListProtocol sync() {
        return new GeoListProtocol(this.storage, true, null);
    }

    public GeoListProtocol share(UUID tasklistId) {
        return new GeoListProtocol(this.storage, true, tasklistId);
    }

    public GeoListProtocol slave() {
        return new GeoListProtocol(this.storage, false, null);
    }
}
