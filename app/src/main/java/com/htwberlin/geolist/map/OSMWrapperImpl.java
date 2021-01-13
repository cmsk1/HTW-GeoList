package com.htwberlin.geolist.map;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.InputDevice;
import android.view.Menu;
import android.view.MotionEvent;

import androidx.core.content.res.ResourcesCompat;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.data.interfaces.LocationRepository;
import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.logic.GeoListLogic;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class OSMWrapperImpl implements OSMWrapper {
    private final MapView mapView;

    private static final int MENU_ABOUT = Menu.FIRST + 1;
    private static final int MENU_LAST_ID = MENU_ABOUT + 1;

    public OSMWrapperImpl(Context context, MapView givenMapView) {

        Context ctx = context.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        LocationRepository locationRepository = GeoListLogic.getStorage().getLocationRepo();

        mapView = givenMapView;
        mapView.setDestroyMode(false);
        mapView.setTag("mapView");

        mapView.setOnGenericMotionListener((v, event) -> {
            if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL) {
                    if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
                        mapView.getController().zoomOut();
                    else {
                        //this part just centers the map on the current mouse location before the zoom action occurs
                        IGeoPoint iGeoPoint = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                        mapView.getController().animateTo(iGeoPoint);
                        mapView.getController().zoomIn();
                    }
                    return true;
                }
            }
            return false;
        });

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(12.0);
        GeoPoint startPoint = new GeoPoint(52.516274579813505, 13.377730920429345); // Brandenburger Tor
        mapController.setCenter(startPoint);

        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), mapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setDrawAccuracyEnabled(true);
        mapView.getOverlays().add(mLocationOverlay);


        for (MarkerLocation location : locationRepository.getAllLocations()) {
            GeoPoint tmp = new GeoPoint(location.getLatitude(), location.getLongitude());

            Marker startMarker = new Marker(mapView);
            startMarker.setPosition(tmp);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_edit_location_alt_50, null));
            mapView.getOverlays().add(startMarker);
        }

        //support for map rotation
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(mapView);
        mRotationGestureOverlay.setEnabled(true);
        mapView.getOverlays().add(mRotationGestureOverlay);
        mapView.setMultiTouchControls(true);
        mapView.setTilesScaledToDpi(true);

    }

    @Override
    public MapView getMapView() {
        return mapView;
    }


}
