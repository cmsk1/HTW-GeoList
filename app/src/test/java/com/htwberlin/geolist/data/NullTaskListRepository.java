package com.htwberlin.geolist.data;

import com.htwberlin.geolist.data.interfaces.TaskListRepository;
import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.models.Task;
import com.htwberlin.geolist.data.models.TaskList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NullTaskListRepository implements TaskListRepository {
    private final Map<Long, TaskList> tasklists = new HashMap<>();
    private final Map<Long, Task> tasks = new HashMap<>();
    private long tasklistCounter = 1;
    private long taskCounter = 1;

    @Override
    public void addList(String displayName) {
        this.addList(displayName, UUID.randomUUID());
    }

    @Override
    public void addList(String displayName, UUID listUuid) {
        long id = this.tasklistCounter++;
        TaskList list = new TaskList(listUuid);
        list.setDisplayName(displayName);
        list.setId(id);
        this.tasklists.put(id, list);
    }

    @Override
    public void saveList(TaskList list) {
        TaskList oldList = this.getList(list.getUuid());

        if (oldList != null) {
            list.setId(oldList.getId());
        } else {
            long id = this.tasklistCounter++;
            list.setId(id);
        }
        this.tasklists.put(list.getId(), list);
    }

    @Override
    public void deleteList(UUID listId) {
        this.tasklists.remove(listId);
    }

    @Override
    public TaskList getList(UUID listId) {
        for (TaskList list : this.tasklists.values()) {
            if (list.getUuid().equals(listId)) {
                return list;
            }
        }
        return null;
    }

    @Override
    public Collection<TaskList> getAllLists() {
        return this.tasklists.values().stream().filter((tasklist) -> !tasklist.isDeleted()).collect(Collectors.toList());
    }

    @Override
    public Collection<TaskList> getAllListsWithDeleted() {
        return this.tasklists.values();
    }

    @Override
    public void addTask(UUID listId, String description) {
        long id = this.taskCounter++;
        TaskList list = this.getList(listId);
        Task task = new Task(UUID.randomUUID());
        task.setId(id);
        task.setDescription(description);
        task.setTaskListId(list.getId());
        list.getTasks().add(task);
        this.tasks.put(id, task);
    }

    @Override
    public void saveTask(Task task) {
        TaskList list = this.tasklists.get(task.getTaskListId());
        Task oldTask = this.getTask(task.getUuid());

        if (oldTask != null) {
            task.setId(oldTask.getId());
        } else {
            long id = this.taskCounter++;
            task.setId(id);
        }
        task.setTaskListId(list.getId());
        this.tasks.put(task.getId(), task);
    }

    @Override
    public void saveTask(Task task, UUID listID) {
        TaskList tasklist = this.tasklists.get(listID);
        Task oldTask = this.tasks.get(task.getUuid());

        if (oldTask != null) {
            tasklist.getTasks().remove(oldTask);
        }
        tasklist.getTasks().remove(task);
        tasklist.getTasks().add(task);
        this.tasks.put(task.getId(), task);
    }

    @Override
    public void deleteTask(UUID taskId) {
        Task task = this.getTask(taskId);
        if (task == null)  return;
        TaskList tasklist = this.tasklists.get(task.getTaskListId());
        tasklist.getTasks().remove(task);
        this.tasks.remove(task.getId());
    }

    @Override
    public Task getTask(UUID taskId) {
        for (Task task : this.tasks.values()) {
            if (task.getUuid().equals(taskId)) {
                return task;
            }
        }
        return null;
    }

    @Override
    public Collection<Task> getAllTasks() {
        return this.tasks.values();
    }

    @Override
    public Collection<Task> getAllTasksInList(UUID listUuid) {
        Collection<Task> allTasksWithDeleted = this.tasklists.get(listUuid).getTasks();
        return allTasksWithDeleted.stream().filter((task) -> !task.isDeleted()).collect(Collectors.toList());
    }

    @Override
    public Collection<Task> getAllTasksInListWithDeleted(UUID listId) {
        return this.tasklists.get(listId).getTasks();
    }

    @Override
    public Collection<TaskList> getAllListsSharedWith(String signature) {
        List<TaskList> lists = new ArrayList<>();

        for (TaskList tasklist : this.tasklists.values()) {
            if (tasklist.getSharedUsers().contains(signature)) {
                lists.add(tasklist);
            }
        }
        return lists;
    }

    @Override
    public void addUserToSharedList(UUID listId, String userId) {
        TaskList list = this.getList(listId);
        list.getSharedUsers().add(userId);
    }

    @Override
    public void removeUserFromSharedList(UUID listId, String signature) {
        TaskList list = this.getList(listId);
        list.getSharedUsers().remove(signature);
    }

    @Override
    public boolean isSharedList(UUID listId) {
        TaskList list = this.getList(listId);
        if (list == null) return false;
        return list.getSharedUsers().size() > 0;
    }

    @Override
    public void addNotifyByDate(UUID listId, Date date) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void removeNotifyByDate(UUID listId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addNotifyByLocation(UUID listId, MarkerLocation markerLocation) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void removeNotifyByLocation(UUID listId) {
        throw new RuntimeException("Not implemented");
    }
}
