package com.htwberlin.geolist.logic;

import com.htwberlin.geolist.data.models.Task;

import java.util.Date;

public class TaskMerger {
    private final Task task1;
    private final Task task2;
    private String description;
    private Date changeAt;
    private Date completedAt;

    public TaskMerger(Task task1, Task task2) {
        this.task1 = task1;
        this.task2 = task2;
    }

    public Task merge() throws IllegalArgumentException {
        if (!this.task1.getUuid().equals(this.task2.getUuid())) {
            throw new IllegalArgumentException("unable to merge task with different uuids!");
        }
        boolean completed = this.task1.isCompleted() || this.task2.isCompleted();
        boolean isDeleted = this.task1.isDeleted() || this.task2.isDeleted();
        this.updateByChangedAt();

        Task merged = new Task(this.task1.getUuid());
        merged.setDescription(this.description);
        merged.setCreatedAt(this.task1.getCreatedAt());
        merged.setChangeAt(this.changeAt);
        merged.setCompleted(completed);
        merged.setCompletedDate(this.completedAt);
        merged.setDeleted(isDeleted);
        return merged;
    }

    private void updateByChangedAt() {
        if (this.task1.getChangeAt().before(this.task2.getChangeAt())) {
            this.description = this.task2.getDescription();
            this.changeAt = this.task2.getChangeAt();
            this.completedAt = this.task2.getCompletedDate();
        } else {
            this.description = this.task1.getDescription();
            this.changeAt = this.task1.getChangeAt();
            this.completedAt = this.task1.getCompletedDate();
        }
    }
}
