package com.htwberlin.geolist.data.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class MarkerLocation implements Serializable {
    private static final long serialVersionUID = 8595820315468L;

    private long id;
    private UUID uuid;
    private Date createdAt;
    private double longitude;
    private double latitude;
    private Date lastNotification;

    public MarkerLocation(UUID uuid) {
        this.uuid = uuid;
    }

    public double distanceTo(MarkerLocation that) {
        double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
        double lat1 = Math.toRadians(this.latitude);
        double lon1 = Math.toRadians(this.longitude);
        double lat2 = Math.toRadians(that.latitude);
        double lon2 = Math.toRadians(that.longitude);

        // great circle distance in radians, using law of cosines formula
        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        // each degree on a great circle of Earth is 60 nautical miles
        double nauticalMiles = 60 * Math.toDegrees(angle);
        return (STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles) / 0.0006213711;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getLastNotification() {
        return lastNotification;
    }

    public void setLastNotification(Date lastNotification) {
        this.lastNotification = lastNotification;
    }
}
