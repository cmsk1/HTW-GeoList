package com.htwberlin.geolist.location.interfaces;

import android.location.Location;

import com.htwberlin.geolist.data.interfaces.LocationRepository;
import com.htwberlin.geolist.data.models.MarkerLocation;

import java.util.ArrayList;
import java.util.Collection;

public class LocationFacadeImpl implements LocationFacade {
    private final LocationRepository locationRepository;

    public LocationFacadeImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location getCurrentLocation() {

        MarkerLocation markerLocation = this.getCurrentLocationAsMarker();
        Location tmp = new Location("CURRENT_LOCATION_FROM_DB");
        if (markerLocation != null) {
            tmp.setLongitude(markerLocation.getLongitude());
            tmp.setLatitude(markerLocation.getLatitude());
        }
        return tmp;

    }

    @Override
    public MarkerLocation getCurrentLocationAsMarker() {
        return this.locationRepository.getCurrentLocation();
    }

    @Override
    public ArrayList<MarkerLocation> getLocationsInRadius(double radiusAsMeters) {
        radiusAsMeters = Math.abs(radiusAsMeters);
        Collection<MarkerLocation> markerLocations = this.locationRepository.getAllLocations();
        MarkerLocation currentMarkerLocation = getCurrentLocationAsMarker();
        ArrayList<MarkerLocation> locationsInRadius = new ArrayList<>();
        for (MarkerLocation markerLocation : markerLocations) {
            if (currentMarkerLocation.distanceTo(markerLocation) <= radiusAsMeters) {
                locationsInRadius.add(markerLocation);
            }
        }

        return locationsInRadius;
    }

    @Override
    public void setCurrentLocation(Location location) {
        this.locationRepository.saveCurrentLocation(location.getLatitude(), location.getLongitude());
    }


}
