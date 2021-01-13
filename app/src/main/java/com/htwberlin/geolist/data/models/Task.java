package com.htwberlin.geolist.data.models;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Task implements Serializable {
    private static final long serialVersionUID = -1040156315229365364L;

    private long id;
    private UUID uuid;
    private Date createdAt;
    private String description;
    private boolean completed;
    private boolean isDeleted;
    private Date completedDate;
    private Date changeAt;
    private long taskListId;

    public Task(UUID uuid) {
        this.uuid = uuid;
        this.createdAt = new Date();
        this.changeAt = this.createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public Date getChangeAt() {
        return changeAt;
    }

    public void setChangeAt(Date changeAt) {
        this.changeAt = changeAt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(long taskListId) {
        this.taskListId = taskListId;
    }

    public int getContentHash() {
        return Objects.hash(this.uuid, this.completed, this.changeAt, this.completedDate, this.description, this.isDeleted);
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Task) {
            Task other = (Task)obj;
            return this.uuid.equals(other.uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", createdAt=" + createdAt +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                ", isDeleted=" + isDeleted +
                ", completedDate=" + completedDate +
                ", changeAt=" + changeAt +
                ", taskListId=" + taskListId +
                '}';
    }
}
