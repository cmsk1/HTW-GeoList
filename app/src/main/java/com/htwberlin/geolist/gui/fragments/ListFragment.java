package com.htwberlin.geolist.gui.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.htwberlin.geolist.R;
import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.gui.TasklistViewAdapter;
import com.htwberlin.geolist.logic.GeoListLogic;

public class ListFragment extends Fragment {
    View view;
    DataStorage storage;

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context context = getActivity().getApplicationContext();
        GeoListLogic.makeInstance(context);
        DataStorage storage = GeoListLogic.getStorage();
        this.storage = storage;

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.tasklists);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new TasklistViewAdapter(context, storage, getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        FloatingActionButton addTaskBtn = (FloatingActionButton) rootView.findViewById(R.id.addListBtn);

        addTaskBtn.setOnClickListener(w -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, ListEditFragment.newInstance(""));
            ft.commit();
        });

        FloatingActionButton syncBtn = (FloatingActionButton) rootView.findViewById(R.id.syncBtn);

        syncBtn.setOnClickListener(w -> {
            GeoListLogic.getNetInterface().scheduleSync();
        });

        return rootView;
    }
}