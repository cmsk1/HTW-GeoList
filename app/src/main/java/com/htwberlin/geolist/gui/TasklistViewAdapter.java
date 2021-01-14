package com.htwberlin.geolist.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.data.interfaces.TaskListRepository;
import com.htwberlin.geolist.data.models.TaskList;
import com.htwberlin.geolist.gui.fragments.ListEditFragment;

import org.jetbrains.annotations.NotNull;

public class TasklistViewAdapter extends RecyclerView.Adapter<TasklistViewAdapter.TaskListViewHolder> {
    private final Context context;
    private final TaskListRepository taskRepo;
    private TaskList[] tasklists = new TaskList[0];
    private final FragmentActivity activity;

    public TasklistViewAdapter(Context context, DataStorage storage, FragmentActivity activity) {
        this.context = context;
        this.taskRepo = storage.getTaskRepo();
        this.activity = activity;
        this.updateTasklists();
    }

    @NotNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tasklist_entry, parent, false);
        return new TaskListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
        if (position < this.tasklists.length) {
            TaskList tasklist = this.tasklists[position];
            holder.text.setText(tasklist.getDisplayName());

            holder.body.setOnClickListener(v -> {
                FragmentTransaction ft = this.activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, ListEditFragment.newInstance(tasklist.getUuid().toString()));
                ft.commit();
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.tasklists.length;
    }

    public void updateTasklists() {
        this.tasklists = taskRepo.getAllLists().toArray(new TaskList[0]);
        this.notifyDataSetChanged();
    }

    public static class TaskListViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        final RelativeLayout body;

        public TaskListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.text = itemView.findViewById(R.id.text);
            this.body = itemView.findViewById(R.id.body);
        }
    }
}
