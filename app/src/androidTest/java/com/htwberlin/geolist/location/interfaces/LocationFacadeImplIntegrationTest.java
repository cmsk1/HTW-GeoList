package com.htwberlin.geolist.location.interfaces;

import android.content.Context;
import android.location.Location;

import androidx.test.annotation.UiThreadTest;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.htwberlin.geolist.data.interfaces.LocationRepositoryImpl;
import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.sqlite.DatabaseHelper;
import com.htwberlin.geolist.gui.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LocationFacadeImplIntegrationTest {

    LocationFacadeImpl locationService;
    DatabaseHelper db;
    LocationRepositoryImpl repo;
    Context context;

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule<>(MainActivity.class);


    @Before
    @UiThreadTest
    public void setUp() {

        Context context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.db = new DatabaseHelper(context);
        this.repo = new LocationRepositoryImpl(this.db);

        this.context = context;
        this.locationService =  LocationFacadeFactory.getInstance(context);

        this.repo.addLocation(52.549082432834076, 13.389629446699734); // S
        this.repo.addLocation(52.55102469917334, 13.387706272798464); // Heide
        this.repo.addLocation(52.5162300419509, 13.376862529548767); // Tor
    }

    @After
    @UiThreadTest
    public void tearDown() {
        this.db.clearAllTables();
        this.db.close();
    }


    @Test
    public void testSetLocation() {
        Location cLoc = new Location("TEST");
        cLoc.setLatitude(52.549433624641);
        cLoc.setLongitude(13.413641021332);

        this.locationService.setCurrentLocation(cLoc);
        Location location = this.locationService.getCurrentLocation();
        assertEquals(52.54943362464172, location.getLatitude(), 0.00001);
        assertEquals(13.413641021332374, location.getLongitude(), 0.00001);

    }

    @Test
    public void testGetMarkerInRadiusWithoutItems() {
        Location cLoc = new Location("TEST");
        cLoc.setLatitude(52.549433624641);
        cLoc.setLongitude(13.413641021332);
        this.locationService.setCurrentLocation(cLoc);

        ArrayList<MarkerLocation> markerLocations = this.locationService.getLocationsInRadius(100);

        assertEquals(0, markerLocations.size());
    }

    @Test
    public void testGetMarkerInRadiusWithZwoItems() {

        Location cLoc = new Location("TEST");
        cLoc.setLatitude(52.549433624641);
        cLoc.setLongitude(13.413641021332);
        this.locationService.setCurrentLocation(cLoc);

        ArrayList<MarkerLocation> markerLocations = this.locationService.getLocationsInRadius(3000);

        assertEquals(2, markerLocations.size());

        assertEquals(52.549082432834076, markerLocations.get(0).getLatitude(), 0.00001);
        assertEquals(13.389629446699734, markerLocations.get(0).getLongitude(), 0.00001);

        assertEquals(52.55102469917334, markerLocations.get(1).getLatitude(), 0.00001);
        assertEquals(13.387706272798464, markerLocations.get(1).getLongitude(), 0.00001);
    }
}