package com.htwberlin.geolist.data.interfaces;

import com.htwberlin.geolist.data.models.MarkerLocation;

import java.util.Collection;
import java.util.UUID;

public interface LocationRepository {
    void addLocation(double longitude, double latitude);

    void saveLocation(MarkerLocation markerLocation);

    void deleteLocation(UUID locationUuid);

    void saveCurrentLocation(double lat, double lon);

    MarkerLocation getCurrentLocation();

    MarkerLocation getLocation(UUID locationUuid);

    Collection<MarkerLocation> getAllLocations();
}
