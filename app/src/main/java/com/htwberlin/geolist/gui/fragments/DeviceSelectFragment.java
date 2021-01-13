package com.htwberlin.geolist.gui.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.gui.DeviceViewAdapter;
import com.htwberlin.geolist.logic.GeoListLogic;

import java.util.UUID;

public class DeviceSelectFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private UUID tasklistId;

    public DeviceSelectFragment() {
        // Required empty public constructor
    }

    public static DeviceSelectFragment newInstance(String listId) {
        DeviceSelectFragment fragment = new DeviceSelectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, listId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String listId = getArguments().getString(ARG_PARAM1);
            this.tasklistId = UUID.fromString(listId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_device_select, container, false);

        Runnable onDeviceSelected = () -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, ListFragment.newInstance());
            ft.commit();
        };

        RecyclerView devices = (RecyclerView) v.findViewById(R.id.devices);
        DeviceViewAdapter adapter = new DeviceViewAdapter(getContext().getApplicationContext(), this.tasklistId, onDeviceSelected);
        devices.setAdapter(adapter);
        devices.setLayoutManager(new LinearLayoutManager(getContext()));

        GeoListLogic.getNetInterface().getNetwork().setPeersListener(adapter);

        // Back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, ListEditFragment.newInstance(tasklistId.toString()));
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return v;
    }

    @Override
    public void onDestroyView() {
        GeoListLogic.getNetInterface().getNetwork().setPeersListener(null);
        super.onDestroyView();
    }
}