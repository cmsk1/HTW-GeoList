package com.htwberlin.geolist.data;

import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.data.interfaces.LocationRepository;

public class NullDataStorage implements DataStorage {
    private NullTaskListRepository taskRepo;
    private NullUserRepository userRepo;

    public NullDataStorage() {
        this.taskRepo = new NullTaskListRepository();
        this.userRepo = new NullUserRepository();
    }

    @Override
    public LocationRepository getLocationRepo() {
        return null;
    }

    @Override
    public NullTaskListRepository getTaskRepo() {
        return this.taskRepo;
    }

    @Override
    public NullUserRepository getUserRepo() {
        return this.userRepo;
    }
}
