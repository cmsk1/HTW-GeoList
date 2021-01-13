package com.htwberlin.geolist.gui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.htwberlin.geolist.R;
import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.data.models.TaskList;
import com.htwberlin.geolist.gui.TaskViewAdapter;
import com.htwberlin.geolist.logic.GeoListLogic;
import com.htwberlin.geolist.net.helper.NetInterface;


import java.util.UUID;

public class ListEditFragment extends Fragment {

    private UUID tasklistId;
    private TaskList list = null;
    private static final int MENU_RENAME = Menu.FIRST + 1;
    private static final int MENU_DELETE = MENU_RENAME + 1; // Always set to last unused id
    boolean isNew = true;
    private String newTaskText = "";

    private static final String ARG_PARAM1 = "param1";
    DataStorage storage;

    EditText txt;
    TextView titleText;
    ImageButton btnSaveExistingList;

    public static ListEditFragment newInstance(String uuid) {
        ListEditFragment fragment = new ListEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    public ListEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        storage = GeoListLogic.getStorage();
        String mParam1 = getArguments().getString(ARG_PARAM1);

        View v = inflater.inflate(R.layout.fragment_list_edit, container, false);
        txt = (EditText) v.findViewById(R.id.titleEdit);
        FloatingActionButton btnNotify = (FloatingActionButton) v.findViewById(R.id.btnNotify);
        FloatingActionButton btnSaveList = (FloatingActionButton) v.findViewById(R.id.btnSaveList);
        FloatingActionButton btnShare = (FloatingActionButton) v.findViewById(R.id.btnShare);
        FloatingActionButton btnNewTask = (FloatingActionButton) v.findViewById(R.id.btnNewTask);
        titleText = (TextView) v.findViewById(R.id.titleText);
        btnSaveExistingList = (ImageButton) v.findViewById(R.id.btnSaveExistingList);

        this.tasklistId = null;


        if (mParam1 != null && !mParam1.equals("")) {
            isNew = false;
            btnNotify.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
            btnSaveList.setVisibility(View.VISIBLE);
            btnNewTask.setVisibility(View.VISIBLE);
            btnSaveExistingList.setVisibility(View.GONE);
            titleText.setVisibility(View.VISIBLE);
            txt.setVisibility(View.GONE);
            this.tasklistId = UUID.fromString(mParam1);
            list = storage.getTaskRepo().getList(this.tasklistId);
            txt.setText(list.getDisplayName());
            titleText.setText(list.getDisplayName());
            setHasOptionsMenu(true);

        } else {
            isNew = true;
            btnNotify.setVisibility(View.GONE);
            btnShare.setVisibility(View.GONE);
            btnNewTask.setVisibility(View.GONE);
            btnSaveExistingList.setVisibility(View.GONE);
            btnSaveList.setVisibility(View.VISIBLE);
            txt.setVisibility(View.VISIBLE);
            titleText.setVisibility(View.GONE);
            this.tasklistId = UUID.randomUUID();
        }

        // Back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                FragmentActivity activity = getActivity();
                if (activity == null) return;
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, ListFragment.newInstance());
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        RecyclerView tasks = (RecyclerView) v.findViewById(R.id.tasks);
        tasks.setAdapter(new TaskViewAdapter(getContext().getApplicationContext(), this.tasklistId, storage, getActivity()));
        tasks.setLayoutManager(new LinearLayoutManager(getContext()));


        btnNotify.setOnClickListener(w -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, MapSelectFragment.newInstance(this.tasklistId.toString()));
            ft.commit();
        });


        btnNewTask.setOnClickListener(w -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Neue Aufgabe");
            newTaskText = "";
            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Speichern", (dialog, which) -> {
                newTaskText = input.getText().toString().trim();
                storage.getTaskRepo().addTask(tasklistId, newTaskText);
                tasks.setAdapter(new TaskViewAdapter(getContext().getApplicationContext(), tasklistId, storage, getActivity()));
                tasks.setLayoutManager(new LinearLayoutManager(getContext()));
            });
            builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.cancel());

            builder.show();
        });


        btnSaveList.setOnClickListener(w -> {

            if (isNew) {
                storage.getTaskRepo().addList(txt.getText().toString(), this.tasklistId);
            } else {
                list.setDisplayName(txt.getText().toString());
                if (list.getDisplayName() != null && list.getDisplayName().trim().length() > 0)
                    storage.getTaskRepo().saveList(list);
            }

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, ListFragment.newInstance());
            ft.commit();
        });

        btnSaveExistingList.setOnClickListener(w -> {

            if (!isNew) {
                list.setDisplayName(txt.getText().toString());
                if (list.getDisplayName() != null && list.getDisplayName().trim().length() > 0)
                    storage.getTaskRepo().saveList(list);
            }
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, ListFragment.newInstance());
            ft.commit();
        });

        btnShare.setOnClickListener(w -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, DeviceSelectFragment.newInstance(this.tasklistId.toString()));
            ft.commit();
        });

        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.add(0, MENU_RENAME, Menu.CATEGORY_SECONDARY, "Liste Umbenennen").setIcon(
                android.R.drawable.ic_menu_edit);
        menu.add(0, MENU_DELETE, Menu.CATEGORY_SECONDARY, "Liste löschen").setIcon(
                android.R.drawable.ic_delete);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == MENU_RENAME) {
            titleText.setVisibility(View.GONE);
            btnSaveExistingList.setVisibility(View.VISIBLE);
            txt.setVisibility(View.VISIBLE);
            return true;
        } else if (item.getItemId() == MENU_DELETE) {
            storage.getTaskRepo().deleteList(tasklistId);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, ListFragment.newInstance());
            ft.commit();
            Toast toast = Toast.makeText(getContext(), "Liste wurde gelöscht", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}