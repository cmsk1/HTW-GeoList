package com.htwberlin.geolist.data.interfaces;

import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.models.SharedUser;
import com.htwberlin.geolist.data.models.Task;
import com.htwberlin.geolist.data.models.TaskList;
import com.htwberlin.geolist.data.models.User;
import com.htwberlin.geolist.data.sqlite.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskListRepositoryImpl implements TaskListRepository {
    private final DatabaseHelper db;

    public TaskListRepositoryImpl(DatabaseHelper db) {
        this.db = db;
    }

    @Override
    public void addList(String displayName) {
        if (displayName == null)
            throw new IllegalArgumentException();

        TaskList taskList = new TaskList(UUID.randomUUID());
        taskList.setDisplayName(displayName.trim());

        this.db.createList(taskList);
    }

    @Override
    public void addList(String displayName, UUID uuid) {
        if (displayName == null || uuid == null)
            throw new IllegalArgumentException();

        TaskList taskList = new TaskList(uuid);
        taskList.setDisplayName(displayName.trim());

        this.db.createList(taskList);
    }

    @Override
    public void saveList(TaskList taskList) {
        if (taskList == null)
            throw new IllegalArgumentException();

        TaskList oldList = this.db.getListByUUID(taskList.getUuid());

        if (oldList == null) {
            long listId = this.db.createList(taskList);
            taskList.setId(listId);
        } else {
            taskList.setId(oldList.getId());
        }

        for (Task task : taskList.getTasks()) {
            saveTask(task, taskList.getUuid());
        }

        for (String userSign : taskList.getSharedUsers()) {
            this.db.saveShare(taskList, userSign);
        }
        this.db.updateList(taskList);
    }


    @Override
    public void deleteList(UUID listUuid) {
        if (listUuid == null)
            throw new IllegalArgumentException();
        TaskList taskList = this.getList(listUuid);

        if (taskList != null && taskList.getId() > 0) {
            this.db.deleteList(this.getList(listUuid).getId());
            if (taskList.getRememberByLocation() != null) {
                this.db.deleteLocation(taskList.getRememberByLocation().getId());
            }
        }
    }

    @Override
    public TaskList getList(UUID listUuid) {
        TaskList list = this.db.getListByUUID(listUuid);
        ArrayList<SharedUser> sharedUsers = this.db.getSharedUsers();
        sharedUsers = sharedUsers.stream().filter(i -> i.getListId() == list.getId()).collect(Collectors.toCollection(ArrayList::new));

        for (SharedUser shared : sharedUsers) {
            list.getSharedUsers().add(shared.getSignature());
        }
        return list;
    }

    @Override
    public ArrayList<TaskList> getAllLists() {
        ArrayList<TaskList> arrayList = this.db.getAllLists();
        ArrayList<SharedUser> allSharedUsers = this.db.getSharedUsers();

        for (TaskList list : arrayList) {
            ArrayList<SharedUser> sharedUsers = allSharedUsers.stream().filter(i -> i.getListId() == list.getId()).collect(Collectors.toCollection(ArrayList::new));

            for (SharedUser shared : sharedUsers) {
                list.getSharedUsers().add(shared.getSignature());
            }
        }
        return arrayList;
    }

    @Override
    public ArrayList<TaskList> getAllListsWithDeleted() {
        ArrayList<TaskList> arrayList = this.db.getAllListsWithDeleted();
        ArrayList<SharedUser> allSharedUsers = this.db.getSharedUsers();

        for (TaskList list : arrayList) {
            ArrayList<SharedUser> sharedUsers = allSharedUsers.stream().filter(i -> i.getListId() == list.getId()).collect(Collectors.toCollection(ArrayList::new));

            for (SharedUser shared : sharedUsers) {
                list.getSharedUsers().add(shared.getSignature());
            }
        }
        return arrayList;
    }

    @Override
    public Collection<TaskList> getAllListsSharedWith(String signature) {
        ArrayList<TaskList> arrayList = this.getAllListsWithDeleted();
        ArrayList<TaskList> validList = new ArrayList<>();

        for (TaskList list : arrayList) {
            if (list.getSharedUsers().contains(signature)) {
                validList.add(list);
            }
        }
        return validList;
    }

    @Override
    public void addTask(UUID listId, String description) {
        if (description == null || listId == null)
            throw new IllegalArgumentException();

        Task task = new Task(UUID.randomUUID());
        task.setDescription(description.trim());

        this.db.createTask(task, this.db.getListByUUID(listId).getId());
    }

    @Override
    public void saveTask(Task task) {
        if (task == null)
            throw new IllegalArgumentException();

        this.db.updateTask(task);
    }

    @Override
    public void saveTask(Task task, UUID listID) {
        if (task == null)
            throw new IllegalArgumentException();

        TaskList list = this.db.getListByUUID(listID);
        Task oldTask = this.db.getTaskByUUID(task.getUuid());

        if (oldTask == null) {
            long taskId = this.db.createTask(task, list.getId());
            task.setId(taskId);
        } else {
            task.setId(oldTask.getId());
        }
        this.db.updateTask(task, listID);
    }

    @Override
    public void deleteTask(UUID uuid) {
        if (uuid == null)
            throw new IllegalArgumentException();

        this.db.deleteTask(getTask(uuid).getId());
    }

    @Override
    public Task getTask(UUID taskUuid) {
        return this.db.getTaskByUUID(taskUuid);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return this.db.getAllTasks();
    }

    @Override
    public ArrayList<Task> getAllTasksInList(UUID listUuid) {
        if (listUuid == null)
            return new ArrayList<Task>();

        TaskList list = this.db.getListByUUID(listUuid);
        long listID = -100;
        if (list != null) {
            listID = this.db.getListByUUID(listUuid).getId();
        }
        return this.db.getAllTasksFromList(listID);
    }

    @Override
    public ArrayList<Task> getAllTasksInListWithDeleted(UUID listUuid) {
        if (listUuid == null)
            return new ArrayList<Task>();

        TaskList list = this.db.getListByUUID(listUuid);
        long listID = -100;
        if (list != null) {
            listID = this.db.getListByUUID(listUuid).getId();
        }
        return this.db.getAllTasksFromListWithDeleted(listID);
    }

    @Override
    public void addNotifyByDate(UUID listUuid, Date date) {
        if (listUuid == null || date == null)
            throw new IllegalArgumentException();

        TaskList list = this.db.getListByUUID(listUuid);
        list.setRememberByDate(date);
        this.saveList(list);
    }

    @Override
    public void removeNotifyByDate(UUID listUuid) {
        if (listUuid == null)
            throw new IllegalArgumentException();

        TaskList list = this.db.getListByUUID(listUuid);
        list.setRememberByDate(null);
        this.saveList(list);
    }

    @Override
    public void addNotifyByLocation(UUID listUuid, MarkerLocation markerLocation) {
        if (listUuid == null || markerLocation == null)
            throw new IllegalArgumentException();

        TaskList list = this.db.getListByUUID(listUuid);
        list.setRememberByLocation(markerLocation);
        this.saveList(list);
    }

    @Override
    public void removeNotifyByLocation(UUID listUuid) {
        if (listUuid == null)
            throw new IllegalArgumentException();

        TaskList list = this.db.getListByUUID(listUuid);
        Optional.ofNullable(list.getRememberByLocation())
                .map(MarkerLocation::getId)
                .ifPresent(this.db::deleteLocation);
        list.setRememberByLocation(null);
        this.saveList(list);
    }

    @Override
    public void addUserToSharedList(UUID listId, String signature) {
        if (listId == null || signature == null)
            throw new IllegalArgumentException();

        TaskList list = this.db.getListByUUID(listId);
        this.db.saveShare(list, signature);
    }

    @Override
    public void removeUserFromSharedList(UUID listId, String signature) {
        if (listId == null || signature == null)
            throw new IllegalArgumentException();

        TaskList list = this.db.getListByUUID(listId);
        this.db.removeShare(list, signature);
    }

    @Override
    public boolean isSharedList(UUID listId) {
        if (listId == null)
            return false;

        TaskList list = this.getList(listId);
        return list.getSharedUsers().size() > 0;
    }
}
