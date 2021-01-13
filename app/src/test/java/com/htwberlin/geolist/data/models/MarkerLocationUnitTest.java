package com.htwberlin.geolist.data.models;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MarkerLocationUnitTest {
    @Test
    public void testLocationDistanceToIsCorrectInMeters(){
        MarkerLocation lo1 = new MarkerLocation(UUID.randomUUID());
        // S Gesundbrunnen
        lo1.setLatitude(52.548844);
        lo1.setLongitude(13.390129);
        MarkerLocation lo2 = new MarkerLocation(UUID.randomUUID());
        // S Schönhauser Allee
        lo2.setLatitude(52.549440);
        lo2.setLongitude(13.413764);

        // etwa 1,6km Luftlinie
        assertEquals(1598.3882323336102, lo1.distanceTo(lo2));
    }
}