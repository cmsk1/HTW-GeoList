package com.htwberlin.geolist.data.interfaces;

import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.models.Task;
import com.htwberlin.geolist.data.models.TaskList;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public interface TaskListRepository {
    void addList(String displayName);

    void addList(String displayName, UUID uuid);

    void saveList(TaskList list);

    void deleteList(UUID listId);

    TaskList getList(UUID listId);

    Collection<TaskList> getAllLists();

    Collection<TaskList> getAllListsWithDeleted();

    Collection<TaskList> getAllListsSharedWith(String signature);

    void addTask(UUID listId, String description);

    void saveTask(Task task);

    void saveTask(Task task, UUID listID);

    void deleteTask(UUID taskId);

    Task getTask(UUID taskId);

    Collection<Task> getAllTasks();

    Collection<Task> getAllTasksInList(UUID listId);

    Collection<Task> getAllTasksInListWithDeleted(UUID listId);

    void addUserToSharedList(UUID listId, String signature);

    void removeUserFromSharedList(UUID listId, String signature);

    boolean isSharedList(UUID listId);

    void addNotifyByDate(UUID listId, Date date);

    void removeNotifyByDate(UUID listId);

    void addNotifyByLocation(UUID listId, MarkerLocation markerLocation);

    void removeNotifyByLocation(UUID listId);
}
