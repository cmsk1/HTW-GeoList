package com.htwberlin.geolist.gui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.map.OSMWrapperImpl;

import org.osmdroid.views.MapView;

public class MapFragment extends Fragment {
    private static final int MENU_ABOUT = Menu.FIRST + 1;
    private static final int MENU_LAST_ID = MENU_ABOUT + 1; // Always set to last unused id
    private MapView mMapView;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        OSMWrapperImpl osm = new OSMWrapperImpl(getActivity().getApplicationContext(), rootView.findViewById(R.id.map));
        this.mMapView = osm.getMapView();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMapView.getOverlayManager().onCreateOptionsMenu(menu, MENU_LAST_ID, mMapView);
        menu.add(0, MENU_ABOUT, Menu.CATEGORY_SECONDARY, R.string.about).setIcon(
                android.R.drawable.ic_menu_info_details);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu pMenu) {
        mMapView.getOverlayManager().onPrepareOptionsMenu(pMenu, MENU_LAST_ID, mMapView);
        super.onPrepareOptionsMenu(pMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMapView.getOverlayManager().onOptionsItemSelected(item, MENU_LAST_ID, mMapView)) {
            return true;
        }
        if (item.getItemId() == MENU_ABOUT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.app_name).setMessage(R.string.about_message)
                    .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                                //
                            }
                    );
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}