package com.htwberlin.geolist.data.interfaces;

import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.sqlite.DatabaseHelper;

import java.util.ArrayList;
import java.util.UUID;

public class LocationRepositoryImpl implements LocationRepository {
    private final DatabaseHelper db;

    public LocationRepositoryImpl(DatabaseHelper db) {
        this.db = db;
    }

    @Override
    public void addLocation(double latitude, double longitude) {
        this.db.createLocation(UUID.randomUUID(), longitude, latitude);
    }

    @Override
    public void saveLocation(MarkerLocation markerLocation) {
        if (markerLocation == null)
            throw new IllegalArgumentException();

        MarkerLocation oldLocation = this.getLocation(markerLocation.getUuid());

        if (oldLocation == null) {
            long id = this.db.createLocation(markerLocation.getUuid(), markerLocation.getLongitude(), markerLocation.getLatitude());
            markerLocation.setId(id);
        } else {
            markerLocation.setId(oldLocation.getId());
            markerLocation.setCreatedAt(oldLocation.getCreatedAt());
            markerLocation.setLastNotification(markerLocation.getLastNotification());
        }
        this.db.updateLocation(markerLocation);
    }

    @Override
    public void deleteLocation(UUID locationUuid) {
        if (locationUuid == null)
            throw new IllegalArgumentException();
        MarkerLocation markerLocation = this.getLocation(locationUuid);

        if (markerLocation != null && markerLocation.getId() > 0)
            this.db.deleteLocation(this.getLocation(locationUuid).getId());
    }

    @Override
    public MarkerLocation getLocation(UUID locationUuid) {
        if (locationUuid == null)
            throw new IllegalArgumentException();

        return this.db.getLocation(locationUuid);
    }

    @Override
    public ArrayList<MarkerLocation> getAllLocations() {
        return this.db.getAllLocations();
    }

    @Override
    public void saveCurrentLocation(double lat, double lon) {
        this.db.setCurrentLocation(lat, lon);
    }

    @Override
    public MarkerLocation getCurrentLocation() {
        return this.db.getCurrentLocation();
    }
}
