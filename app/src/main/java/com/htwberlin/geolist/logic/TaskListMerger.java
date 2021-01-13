package com.htwberlin.geolist.logic;

import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.models.Task;
import com.htwberlin.geolist.data.models.TaskList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class TaskListMerger {
    private final TaskList list1;
    private final TaskList list2;
    private String displayName;
    private boolean isDeleted;
    private Date changesAt;
    private Date rememberTime;
    private MarkerLocation rememberLocation;
    private Collection<Task> tasks = new ArrayList<>();

    public TaskListMerger(TaskList list1, TaskList list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    public TaskList merge() throws IllegalArgumentException {
        if (!list1.getUuid().equals(list2.getUuid())) {
            throw new IllegalArgumentException("unable to merge tasklists with different uuids");
        }
        this.isDeleted = this.list1.isDeleted() || this.list2.isDeleted();
        this.updateByChangesAt();
        this.mergeTasks();

        TaskList merged = new TaskList(list1.getUuid());
        merged.setCreatedAt(this.list1.getCreatedAt());
        merged.setDisplayName(this.displayName);
        merged.setDeleted(this.isDeleted);
        merged.setChangesAt(this.changesAt);
        merged.setRememberByDate(this.rememberTime);
        merged.setRememberByLocation(this.rememberLocation);
        merged.setTasks(this.tasks);
        return merged;
    }

    private void mergeTasks() throws IllegalArgumentException {
        List<Task> subtasks1 = new ArrayList<>(this.list1.getTasks());
        List<Task> subtasks2 = new ArrayList<>(this.list2.getTasks());

        for (int i = subtasks1.size() - 1; i >= 0; i--) {
            Task subtask1 = subtasks1.get(i);

            for (int j = subtasks2.size() - 1; j >= 0; j--) {
                Task subtask2 = subtasks2.get(j);

                if (subtask1.getUuid().equals(subtask2.getUuid())) {
                    TaskMerger merger = new TaskMerger(subtask1, subtask2);
                    this.tasks.add(merger.merge());
                    subtasks1.remove(i);
                    subtasks2.remove(j);
                    break;
                }
            }
        }
        this.tasks.addAll(subtasks1);
        this.tasks.addAll(subtasks2);
    }

    private void updateByChangesAt() {
        if (this.list1.getChangesAt().before(this.list2.getChangesAt())) {
            this.displayName = this.list2.getDisplayName();
            this.changesAt = this.list2.getChangesAt();
            this.rememberTime = this.list2.getRememberByDate();
            this.rememberLocation = this.list2.getRememberByLocation();
        } else {
            this.displayName = this.list1.getDisplayName();
            this.changesAt = this.list1.getChangesAt();
            this.rememberTime = this.list1.getRememberByDate();
            this.rememberLocation = this.list1.getRememberByLocation();
        }
    }
}
