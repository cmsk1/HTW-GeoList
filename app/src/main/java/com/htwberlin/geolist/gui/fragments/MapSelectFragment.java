package com.htwberlin.geolist.gui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.models.TaskList;
import com.htwberlin.geolist.gui.activity.MainActivity;
import com.htwberlin.geolist.logic.GeoListLogic;
import com.htwberlin.geolist.map.OSMWrapperImpl;

import org.osmdroid.views.MapView;

import java.util.UUID;

public class MapSelectFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final int MENU_ABOUT = Menu.FIRST + 1;
    private static final int MENU_LAST_ID = MENU_ABOUT + 1; // Always set to last unused id
    private MapView mMapView;
    UUID taskListId;

    public MapSelectFragment() {
        // Required empty public constructor
    }

    public static MapSelectFragment newInstance(String uuid) {
        MapSelectFragment fragment = new MapSelectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uuid);
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
        View rootView = inflater.inflate(R.layout.fragment_map_select, container, false);
        Button btnSaveMarkerForList = (Button) rootView.findViewById(R.id.btnSaveMarkerForList);
        Button btnDeleteMarker = (Button) rootView.findViewById(R.id.btnDeleteMarker);
        taskListId = UUID.fromString(getArguments().getString(ARG_PARAM1));

        OSMWrapperImpl osm = new OSMWrapperImpl(getActivity().getApplicationContext(), rootView.findViewById(R.id.map));
        this.mMapView = osm.getMapView();

        // Back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, ListFragment.newInstance());
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);


        // Button Actions
        DataStorage storage = GeoListLogic.getStorage();
        TaskList list = storage.getTaskRepo().getList(this.taskListId);

        btnDeleteMarker.setOnClickListener(w -> {
            if (list.getRememberByLocation() != null) {
                storage.getTaskRepo().removeNotifyByLocation(taskListId);
            }
            Intent intent = new Intent(getContext(), MainActivity.class);
            this.startActivity(intent);
            Toast toast = Toast.makeText(getContext(), "Gespeichert", Toast.LENGTH_SHORT);
            toast.show();
        });
        btnSaveMarkerForList.setOnClickListener(w -> {
            MarkerLocation markerLocation = new MarkerLocation(UUID.randomUUID());
            markerLocation.setLongitude(this.mMapView.getMapCenter().getLongitude());
            markerLocation.setLatitude(this.mMapView.getMapCenter().getLatitude());

            if (list.getRememberByLocation() != null) {
                storage.getTaskRepo().removeNotifyByLocation(taskListId);
            }
            storage.getTaskRepo().addNotifyByLocation(taskListId, markerLocation);

            Intent intent = new Intent(getContext(), MainActivity.class);
            this.startActivity(intent);
            Toast toast = Toast.makeText(getContext(), "Gespeichert", Toast.LENGTH_SHORT);
            toast.show();
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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