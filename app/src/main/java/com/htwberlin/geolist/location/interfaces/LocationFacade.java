package com.htwberlin.geolist.location.interfaces;

import android.location.Location;

import com.htwberlin.geolist.data.models.MarkerLocation;

import java.util.ArrayList;

public interface LocationFacade {
    Location getCurrentLocation();

    void setCurrentLocation(Location location);

    MarkerLocation getCurrentLocationAsMarker();

    ArrayList<MarkerLocation> getLocationsInRadius(double radiusAsMeters);

}
