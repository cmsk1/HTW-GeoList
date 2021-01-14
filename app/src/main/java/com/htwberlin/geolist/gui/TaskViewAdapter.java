package com.htwberlin.geolist.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.data.interfaces.TaskListRepository;
import com.htwberlin.geolist.data.models.Task;
import com.htwberlin.geolist.gui.fragments.ListEditFragment;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.TaskViewHolder> {
    private final Context context;
    private final TaskListRepository taskRepo;
    private final UUID tasklistUuid;
    private final FragmentActivity activity;
    private Task[] tasks = new Task[0];

    public TaskViewAdapter(Context context, UUID tasklistUuid, DataStorage storage, FragmentActivity activity) {
        this.context = context;
        this.tasklistUuid = tasklistUuid;
        this.taskRepo = storage.getTaskRepo();
        this.activity = activity;
        this.makeSnapshot();
    }

    @NotNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_entry, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (position < this.tasks.length) {
            Task task = this.tasks[position];
            holder.text.setText(task.getDescription());
            holder.checkBox.setChecked(task.isCompleted());
            holder.checkBox.setContentDescription("checkBox_" + position);
            holder.btnEdit.setContentDescription("editTask_" + position);
            holder.checkBox.setOnClickListener(v -> {
                task.setCompleted(holder.checkBox.isChecked());
                taskRepo.saveTask(task);
            });
            holder.btnEdit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Aufgabe bearbeiten");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(task.getDescription());
                builder.setView(input);

                builder.setPositiveButton("Speichern", (dialog, which) -> {
                    task.setDescription(input.getText().toString().trim());
                    taskRepo.saveTask(task);
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.nav_host_fragment, ListEditFragment.newInstance(tasklistUuid.toString()));
                    ft.commit();
                });
                builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.cancel());
                builder.setNeutralButton("Löschen!", (dialog, which) -> {
                    taskRepo.deleteTask(task.getUuid());
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.nav_host_fragment, ListEditFragment.newInstance(tasklistUuid.toString()));
                    ft.commit();
                });

                builder.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.tasks.length;
    }

    private void makeSnapshot() {
        this.tasks = taskRepo.getAllTasksInList(tasklistUuid).toArray(new Task[0]);
        this.notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        final CheckBox checkBox;
        final ImageButton btnEdit;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.text = itemView.findViewById(R.id.descEdit);
            this.checkBox = itemView.findViewById(R.id.checkBox);
            this.btnEdit = itemView.findViewById(R.id.taskEntryEdit);
        }
    }
}
