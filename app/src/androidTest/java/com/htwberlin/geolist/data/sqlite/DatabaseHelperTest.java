package com.htwberlin.geolist.data.sqlite;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.models.Task;
import com.htwberlin.geolist.data.models.TaskList;
import com.htwberlin.geolist.data.models.User;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    DatabaseHelper db;

    @Before
    public void setUp() {
        Context context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.db = new DatabaseHelper(context);
    }

    @After
    public void tearDown() {
        this.db.clearAllTables();
        this.db.close();
    }

    @Test
    public void testInitializeDatabase() {
        assertEquals("geoList", this.db.getDatabaseName());
        assertTrue(this.db.getReadableDatabase().isOpen());
    }

    @Test
    public void testGetEmptyValues() {
        assertTrue(this.db.getAllLists().isEmpty());
        assertTrue(this.db.getAllLocations().isEmpty());
        assertTrue(this.db.getAllTasks().isEmpty());
        assertTrue(this.db.getAllUsers().isEmpty());
        assertNull(this.db.getList(1));
        assertNull(this.db.getLocation(1));
        assertNull(this.db.getTask(1));
        assertNull(this.db.getUser(1));
        assertTrue(this.db.getSharedUsers().isEmpty());
    }

    @Test
    public void testTaskLists() {
        assertTrue(this.db.getAllTasks().isEmpty());
        assertTrue(this.db.getAllLists().isEmpty());
        assertTrue(this.db.getAllUsers().isEmpty());

        this.db.createUser("b3517e11-3e77-4dfd-bd51-6990d5d0151d","John Doe");
        User u1 = this.db.getAllUsers().get(0);

        assertEquals("b3517e11-3e77-4dfd-bd51-6990d5d0151d", u1.getSignature());
        assertEquals("John Doe", u1.getDisplayName());

        TaskList l1 = new TaskList(UUID.randomUUID());
        l1.setDisplayName("List 1");
        l1.setOwned(true);

        this.db.createList(l1);

        assertEquals(1, this.db.getAllLists().size());

        l1 = this.db.getAllLists().get(0);

        assertEquals("List 1", l1.getDisplayName());
        assertTrue(l1.isOwned());

        Task t1 = new Task(UUID.randomUUID());
        t1.setDescription("Test 1");
        
        Task t2 = new Task(UUID.randomUUID());
        t2.setDescription("Test 2");

        this.db.createTask(t1, l1.getId());
        this.db.createTask(t2, l1.getId());

        assertEquals(l1.getId(), this.db.getAllTasks().get(0).getTaskListId());
        assertEquals(l1.getId(), this.db.getAllTasks().get(1).getTaskListId());
        assertEquals("Test 1", this.db.getAllTasks().get(0).getDescription());
        assertEquals("Test 2", this.db.getAllTasks().get(1).getDescription());

        l1 = this.db.getAllLists().get(0);

        assertEquals("Test 1", l1.getTasks().toArray(new Task[0])[0].getDescription());
        assertEquals("Test 2", l1.getTasks().toArray(new Task[0])[1].getDescription());

    }


    @Test
    public void testUpdateTask() {
        this.db.createUser("b3517e11-3e77-4dfd-bd51-6990d5d0151d","John Doe");
        User u1 = this.db.getAllUsers().get(0);

        assertEquals("b3517e11-3e77-4dfd-bd51-6990d5d0151d", u1.getSignature());
        assertEquals("John Doe", u1.getDisplayName());

        TaskList l1 = new TaskList(UUID.randomUUID());
        l1.setDisplayName("List 1");

        this.db.createList(l1);

        assertEquals(1, this.db.getAllLists().size());

        l1 = this.db.getAllLists().get(0);

        assertEquals("List 1", l1.getDisplayName());
        assertTrue(l1.isOwned());

        Task t1 = new Task(UUID.randomUUID());
        t1.setDescription("Test 1");

        this.db.createTask(t1, l1.getId());

        assertEquals(l1.getId(), this.db.getAllTasks().get(0).getTaskListId());
        t1 = this.db.getAllTasks().get(0);
        assertEquals("Test 1", t1.getDescription());
        assertNull(t1.getCompletedDate());
        assertFalse(t1.isCompleted());

        t1.setCompleted(true);
        t1.setDescription("Neuer Name");

        this.db.updateTask(t1);
        t1 = this.db.getAllTasks().get(0);
        assertEquals("Neuer Name", t1.getDescription());
        assertTrue(t1.isCompleted());
        assertNotNull(t1.getCompletedDate());

    }

    @Test
    public void testDeleteTaskAndGetAllShouldNotReturnDeletedValues() {
        this.db.createUser("b3517e11-3e77-4dfd-bd51-6990d5d0151d","John Doe");
        User u1 = this.db.getAllUsers().get(0);

        assertEquals("b3517e11-3e77-4dfd-bd51-6990d5d0151d", u1.getSignature());
        assertEquals("John Doe", u1.getDisplayName());

        TaskList l1 = new TaskList(UUID.randomUUID());
        l1.setDisplayName("List 1");
        this.db.createList(l1);

        assertEquals(1, this.db.getAllLists().size());

        l1 = this.db.getAllLists().get(0);
        assertEquals("List 1", l1.getDisplayName());
        assertTrue(l1.isOwned());

        Task t1 = new Task(UUID.randomUUID());
        t1.setDescription("Test 1");

        this.db.createTask(t1, l1.getId());
        t1 = this.db.getAllTasks().get(0);

        assertEquals(1, this.db.getAllTasks().size());
        assertFalse(this.db.getAllTasks().get(0).isDeleted());

        this.db.deleteTask(t1.getId());

        assertEquals(0, this.db.getAllTasks().size());
    }


    @Test
    public void testUpdateList() {
        this.db.createUser("b3517e11-3e77-4dfd-bd51-6990d5d0151d","John Doe");
        User u1 = this.db.getAllUsers().get(0);

        assertEquals("b3517e11-3e77-4dfd-bd51-6990d5d0151d", u1.getSignature());
        assertEquals("John Doe", u1.getDisplayName());

        TaskList l1 = new TaskList(UUID.randomUUID());
        l1.setDisplayName("List 1");
        l1.setOwned(false);

        this.db.createList(l1);

        assertEquals(1, this.db.getAllLists().size());

        l1 = this.db.getAllLists().get(0);

        assertEquals("List 1", l1.getDisplayName());
        assertNotNull(l1.getCreatedAt());
        assertNull(l1.getRememberByDate());
        assertNull(l1.getRememberByLocation());
        assertEquals(0, l1.getSharedUsers().size());
        assertFalse(l1.isOwned());
        assertFalse(l1.isDeleted());

        l1.setDisplayName("Neue Liste");
        l1.setRememberByDate(new Date());
        l1.setOwned(true);

        MarkerLocation loc = new MarkerLocation(UUID.randomUUID());
        loc.setLongitude(0);
        loc.setLatitude(0);
        l1.setRememberByLocation(loc);

        this.db.updateList(l1);
        l1 = this.db.getAllLists().get(0);

        assertEquals("Neue Liste", l1.getDisplayName());
        assertNotNull(l1.getCreatedAt());
        assertNotNull(l1.getRememberByDate());
        assertEquals(0, l1.getRememberByLocation().getLatitude(), 0.001);
        assertEquals(0, l1.getRememberByLocation().getLongitude(), 0.001);
        assertNotNull(l1.getSharedUsers());
        assertTrue(l1.isOwned());
        assertFalse(l1.isDeleted());
    }

    @Test
    public void testDeleteList() {
        TaskList l1 = new TaskList(UUID.randomUUID());
        this.db.createList(l1);
        assertEquals(1, this.db.getAllLists().size());
        l1 = this.db.getAllLists().get(0);
        this.db.deleteList(l1.getId());
        assertEquals(0, this.db.getAllLists().size());
    }

    @Test
    public void testSaveLocation() {
        long id = this.db.createLocation(UUID.randomUUID(), 11,15);
        MarkerLocation loc = this.db.getLocation(id);
        assertEquals(11, loc.getLongitude(), 0.001);
        assertEquals(15, loc.getLatitude(), 0.001);
        assertNotNull(loc.getCreatedAt());
        assertNotNull(loc.getUuid());
        assertNull(loc.getLastNotification());

        loc.setLongitude(22);
        this.db.updateLocation(loc);

        assertEquals(22, loc.getLongitude(), 0.001);
        assertEquals(15, loc.getLatitude(), 0.001);
        assertNotNull(loc.getCreatedAt());
        assertNotNull(loc.getUuid());
        assertNull(loc.getLastNotification());

        loc.setLastNotification(new Date());
        this.db.updateLocation(loc);

        assertEquals(22, loc.getLongitude(), 0.001);
        assertEquals(15, loc.getLatitude(), 0.001);
        assertNotNull(loc.getCreatedAt());
        assertNotNull(loc.getUuid());
        assertNotNull(loc.getLastNotification());

    }

    @Test
    public void testDeleteLocation() {
        long id = this.db.createLocation(UUID.randomUUID(), 11,15);
        MarkerLocation loc = this.db.getLocation(id);
        assertEquals(1, this.db.getAllLocations().size());
        this.db.deleteLocation(loc.getId());
        assertEquals(0, this.db.getAllLocations().size());
    }


    @Test
    public void testSaveUser() {
        long id = this.db.createUser("0123456","Max Mustermann");
        User user = this.db.getUser(id);
        assertEquals("0123456", user.getSignature());
        assertEquals("Max Mustermann", user.getDisplayName());
        assertEquals(0, user.getLastSync());

        user.setDisplayName("Muster Maxmann");
        this.db.updateUser(user);

        assertEquals("0123456", user.getSignature());
        assertEquals("Muster Maxmann", user.getDisplayName());
        assertEquals(0, user.getLastSync());
    }
    @Test
    public void testSetLastSyncLong() {
        long id = this.db.createUser("0123456","Max Mustermann");
        User user = this.db.getUser(id);
        assertEquals(0, user.getLastSync());

        user.setLastSync(151515151515151511L);
        this.db.updateUser(user);

        assertEquals(151515151515151511L, user.getLastSync());
    }

    @Test
    public void testDeleteUser() {
        long id = this.db.createUser("0123456","Max Mustermann");
        User user = this.db.getUser(id);

        assertEquals(1, this.db.getAllUsers().size());

        this.db.deleteUser(user.getId());

        assertEquals(0, this.db.getAllUsers().size());
    }

    @Test
    public void testSetCurrentLocationShouldSetLocation() {
        this.db.setCurrentLocation(52.123456789,13.987654321);

        assertEquals(52.123456789, this.db.getCurrentLocation().getLatitude(), 0.0000001);
        assertEquals(13.987654321, this.db.getCurrentLocation().getLongitude(), 0.0000001);

    }

    @Test
    public void testSetCurrentLocationShouldSetLocationOnceEvenWhenSetTwoTimes() {
        this.db.setCurrentLocation(12.5454545,18.1251545454);
        this.db.setCurrentLocation(52.123456789,13.987654321);

        assertEquals(52.123456789, this.db.getCurrentLocation().getLatitude(), 0.0000001);
        assertEquals(13.987654321, this.db.getCurrentLocation().getLongitude(), 0.0000001);

    }

    @Test
    public void testGetCurrentLocationIfNoLocationIsSet() {
        assertNotNull(this.db.getCurrentLocation());
    }
    
}