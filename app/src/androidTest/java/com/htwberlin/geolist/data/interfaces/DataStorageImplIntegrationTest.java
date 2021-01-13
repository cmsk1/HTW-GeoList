package com.htwberlin.geolist.data.interfaces;

import android.content.Context;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataStorageImplIntegrationTest {

    @Test
    public void testCreateCorrectRepos(){
        Context context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataStorageImpl dataStorage = new DataStorageImpl(context);

        UserRepository userRepository = dataStorage.getUserRepo();
        TaskListRepository taskListRepository = dataStorage.getTaskRepo();
        LocationRepository locationRepository = dataStorage.getLocationRepo();

        assertTrue(userRepository instanceof UserRepositoryImpl);
        assertTrue(taskListRepository instanceof TaskListRepositoryImpl);
        assertTrue(locationRepository instanceof LocationRepositoryImpl);
    }
}