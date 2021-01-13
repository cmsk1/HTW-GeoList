package com.htwberlin.geolist.data.interfaces;

import android.content.Context;

import com.htwberlin.geolist.data.sqlite.DatabaseHelper;

public class DataStorageImpl implements DataStorage {
    private final LocationRepository locationRepo;
    private final TaskListRepository taskRepo;
    private final UserRepository userRepo;

    public DataStorageImpl(Context context) {
        DatabaseHelper db = new DatabaseHelper(context);
        this.locationRepo = new LocationRepositoryImpl(db);
        this.taskRepo = new TaskListRepositoryImpl(db);
        this.userRepo = new UserRepositoryImpl(db);
    }

    @Override
    public LocationRepository getLocationRepo() {
        return this.locationRepo;
    }

    @Override
    public TaskListRepository getTaskRepo() {
        return this.taskRepo;
    }

    @Override
    public UserRepository getUserRepo() {
        return this.userRepo;
    }
}
