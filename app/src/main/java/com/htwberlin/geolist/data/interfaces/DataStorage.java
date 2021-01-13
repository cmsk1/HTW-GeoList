package com.htwberlin.geolist.data.interfaces;

public interface DataStorage {
    LocationRepository getLocationRepo();

    TaskListRepository getTaskRepo();

    UserRepository getUserRepo();
}
