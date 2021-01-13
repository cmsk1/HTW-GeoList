package com.htwberlin.geolist.data.interfaces;

import android.content.Context;

import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.sqlite.DatabaseHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;


public class LocationRepositoryImplIntegrationTest {

    DatabaseHelper db;
    LocationRepositoryImpl repo;

    @Before
    public void setUp() {
        Context context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.db = new DatabaseHelper(context);
        this.db.clearAllTables();
        this.repo = new LocationRepositoryImpl(this.db);

        this.repo.addLocation(52.548844, 13.390129);
        this.repo.addLocation(53.548844, 14.390129);
    }

    @After
    public void tearDown() {
        this.db.clearAllTables();
        this.db.close();
    }

    @Test
    public void testLocationRepoAddLocationIsSuccessful(){
        MarkerLocation markerLocation = this.repo.getAllLocations().get(0);

        assertEquals(13.390129, markerLocation.getLongitude(), 0.000001);
        assertEquals(52.548844, markerLocation.getLatitude(), 0.000001);
        assertNotNull( markerLocation.getCreatedAt());
        assertNotNull( markerLocation.getUuid());
        assertTrue( markerLocation.getId() > 0);

        this.repo.addLocation(53.548844, 14.390129);
        MarkerLocation markerLocation2 = this.repo.getAllLocations().get(1);

        assertEquals(14.390129, markerLocation2.getLongitude(), 0.000001);
        assertEquals(53.548844, markerLocation2.getLatitude(), 0.000001);
        assertNotNull( markerLocation2.getCreatedAt());
        assertNotNull( markerLocation2.getUuid());
        assertTrue( markerLocation2.getId() > 0);
    }

    @Test
    public void testLocationRepoSaveLocationActuallySavesCorrectAttributes(){
        MarkerLocation markerLocation = this.repo.getAllLocations().get(0);
        UUID testUUID = markerLocation.getUuid();

        markerLocation.setLatitude(40.390129);
        this.repo.saveLocation(markerLocation);
        markerLocation = this.repo.getAllLocations().get(0);

        assertEquals(13.390129, markerLocation.getLongitude(), 0.000001);
        assertEquals(40.390129, markerLocation.getLatitude(), 0.000001);
        assertEquals(testUUID, markerLocation.getUuid());
        assertNotNull( markerLocation.getCreatedAt());
        assertNotNull( markerLocation.getUuid());
        assertTrue( markerLocation.getId() > 0);
    }

    @Test
    public void testLocationRepoDeleteLocationActuallyDeletesObject(){
        MarkerLocation markerLocation = this.repo.getAllLocations().get(0);
        MarkerLocation markerLocation2 = this.repo.getAllLocations().get(1);
        assertEquals(2, this.repo.getAllLocations().size());

        this.repo.deleteLocation(markerLocation.getUuid());
        assertEquals(1, this.repo.getAllLocations().size());
        MarkerLocation markerLocation3 = this.repo.getAllLocations().get(0);
        assertEquals(markerLocation2.getUuid(), markerLocation3.getUuid());
    }

    @Test
    public void testLocationRepoDeleteLocationWithWrongUUIDDeletesNothing() {
        assertEquals(2, this.repo.getAllLocations().size());
        this.repo.deleteLocation(UUID.randomUUID());
        assertEquals(2, this.repo.getAllLocations().size());
    }

    @Test
    public void testLocationIsSetToEmptyMarkerIfNothingIsSet() {
        assertNotNull(this.repo.getCurrentLocation());
    }

    @Test
    public void testLocationIsSavedCorrectly() {
        this.repo.saveCurrentLocation(45.67899,32.878788);
        assertNotNull(this.repo.getCurrentLocation());
        assertEquals(45.67899, this.repo.getCurrentLocation().getLatitude(), 0.00001);
        assertEquals(32.878788, this.repo.getCurrentLocation().getLongitude(), 0.00001);
    }
}