package com.htwberlin.geolist.data.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class TaskList implements Serializable {
    private static final long serialVersionUID = 1740157325820315468L;

    private long id;
    private UUID uuid;
    private Date createdAt;
    private Date changesAt;
    private boolean isOwned;
    private boolean isDeleted;
    private ArrayList<String> sharedUsers;
    private String displayName;
    private ArrayList<Task> tasks;
    private Date rememberByDate;
    private MarkerLocation rememberByMarkerLocation;

    public TaskList(UUID uuid) {
        this(uuid, true, "Unbenannt");
    }

    public TaskList(UUID uuid, boolean isOwned, String displayName) {
        this.uuid = uuid;
        this.isOwned = isOwned;
        this.displayName = displayName;
        this.tasks = new ArrayList<>();
        this.isDeleted = false;
        this.rememberByDate = null;
        this.rememberByMarkerLocation = null;
        this.createdAt = new Date();
        this.changesAt = this.createdAt;
        this.sharedUsers = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getChangesAt() {
        return changesAt;
    }

    public void setChangesAt(Date changesAt) {
        this.changesAt = changesAt;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned(boolean isOwned) {
        this.isOwned = isOwned;
    }

    public Collection<String> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(Collection<String> sharedUsers) {
        this.sharedUsers = new ArrayList<>(sharedUsers);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Collection<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Collection<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public Date getRememberByDate() {
        return rememberByDate;
    }

    public void setRememberByDate(Date rememberByDate) {
        this.rememberByDate = rememberByDate;
    }

    public MarkerLocation getRememberByLocation() {
        return rememberByMarkerLocation;
    }

    public void setRememberByLocation(MarkerLocation rememberByMarkerLocation) {
        this.rememberByMarkerLocation = rememberByMarkerLocation;
    }

    public int getContentHash() {
        int[] taskHashes = new int[this.tasks.size()];
        int index = 0;

        for (Task task : this.tasks) {
            taskHashes[index++] = task.getContentHash();
        }
        return Objects.hash(this.id, this.displayName, this.rememberByDate, this.rememberByMarkerLocation, this.createdAt, this.changesAt, this.isDeleted, taskHashes);
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TaskList) {
            TaskList other = (TaskList)obj;
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
        return "TaskList{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", createdAt=" + createdAt +
                ", changesAt=" + changesAt +
                ", isOwned=" + isOwned +
                ", isDeleted=" + isDeleted +
                ", sharedUsers=" + sharedUsers +
                ", displayName='" + displayName + '\'' +
                ", tasks=" + tasks +
                ", rememberByDate=" + rememberByDate +
                ", rememberByMarkerLocation=" + rememberByMarkerLocation +
                '}';
    }
}
