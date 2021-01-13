package com.htwberlin.geolist.net;

import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.logic.NullGeoListLogic;
import com.htwberlin.geolist.net.protocol.GeoListProtocolEngine;

import java.util.UUID;

public class NullGeoListProtocolEngine extends GeoListProtocolEngine {
    public NullGeoListProtocolEngine(DataStorage storage) {
        super(storage);
    }

    public SpyGeoListProtocol sync() {
        return new SpyGeoListProtocol(this.storage, true, null);
    }

    public SpyGeoListProtocol share(UUID tasklistId) {
        return new SpyGeoListProtocol(this.storage, true, tasklistId);
    }

    public SpyGeoListProtocol slave() {
        return new SpyGeoListProtocol(this.storage, false, null);
    }
}
