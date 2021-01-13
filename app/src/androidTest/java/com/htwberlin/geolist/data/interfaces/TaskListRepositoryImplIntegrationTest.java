package com.htwberlin.geolist.data.interfaces;

import android.content.Context;

import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.models.Task;
import com.htwberlin.geolist.data.models.TaskList;
import com.htwberlin.geolist.data.models.User;
import com.htwberlin.geolist.data.sqlite.DatabaseHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class TaskListRepositoryImplIntegrationTest {

    DatabaseHelper db;
    TaskListRepositoryImpl repo;

    @Before
    public void setUp() {
        Context context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.db = new DatabaseHelper(context);
        this.repo = new TaskListRepositoryImpl(this.db);

        this.repo.addList("Test Liste");
        this.repo.addList("Test Liste 2");
    }

    @After
    public void tearDown() {
        this.db.clearAllTables();
        this.db.close();
    }

    @Test
    public void testListsAddedSuccessfully() {
        TaskList list1 = this.repo.getAllLists().get(0);
        TaskList list2 = this.repo.getAllLists().get(1);

        assertEquals("Test Liste", list1.getDisplayName());
        assertEquals("Test Liste 2", list2.getDisplayName());
        assertNotNull(list1.getCreatedAt());
        assertNotNull(list2.getCreatedAt());
        assertTrue(list1.getId() > 0);
        assertTrue(list2.getId() > 0);
        assertEquals(0, list1.getSharedUsers().size());
        assertEquals(0, list2.getSharedUsers().size());
        assertNull(list1.getRememberByLocation());
        assertNull(list2.getRememberByLocation());
        assertNull(list1.getRememberByDate());
        assertNull(list2.getRememberByDate());
    }

    @Test
    public void testListsUpdate() {
        TaskList list1 = this.repo.getAllLists().get(0);
        Date date = new Date();
        list1.setDisplayName("Noch ein Test");
        list1.setRememberByDate(date);

        this.repo.saveList(list1);

        TaskList list2 = this.repo.getAllLists().get(0);

        assertEquals(list1.getUuid(), list2.getUuid());
        assertEquals("Noch ein Test", list2.getDisplayName());
        assertNotNull(list2.getRememberByDate());
    }

    @Test
    public void testDeleteList() {
        TaskList list1 = this.repo.getAllLists().get(0);
        assertEquals(2, this.repo.getAllLists().size());
        this.repo.deleteList(list1.getUuid());
        assertEquals(1, this.repo.getAllLists().size());
    }

    @Test
    public void testDeleteListWithDeleted() {
        TaskList list1 = this.repo.getAllListsWithDeleted().get(0);
        assertEquals(2, this.repo.getAllListsWithDeleted().size());
        this.repo.deleteList(list1.getUuid());
        assertEquals(2, this.repo.getAllListsWithDeleted().size());

        list1 = this.repo.getAllListsWithDeleted().get(0);

        assertTrue(list1.isDeleted());
    }

    @Test
    public void testAddTask() {
        TaskList list1 = this.repo.getAllLists().get(0);

        assertEquals(0, list1.getTasks().size());
        this.repo.addTask(list1.getUuid(), "TEst");
        list1 = this.repo.getAllLists().get(0);
        assertEquals(1, list1.getTasks().size());

        ArrayList<Task> taskList = new ArrayList<>(list1.getTasks());
        Task task = taskList.get(0);
        assertEquals("TEst", task.getDescription());
        assertFalse(task.isCompleted());
    }

    @Test
    public void testSaveTask() {
        TaskList list1 = this.repo.getAllLists().get(0);

        assertEquals(0, list1.getTasks().size());
        this.repo.addTask(list1.getUuid(), "TEst");
        list1 = this.repo.getAllLists().get(0);
        assertEquals(1, list1.getTasks().size());

        ArrayList<Task> taskList = new ArrayList<>(list1.getTasks());
        Task task = taskList.get(0);
        assertEquals("TEst", task.getDescription());
        assertFalse(task.isCompleted());
        assertNull(task.getCompletedDate());

        task.setDescription("Test Neu");
        task.setCompleted(true);

        repo.saveTask(task);
        list1 = this.repo.getAllLists().get(0);

        taskList = new ArrayList<>(list1.getTasks());
        task = taskList.get(0);
        assertEquals("Test Neu", task.getDescription());
        assertTrue(task.isCompleted());
        assertNotNull(task.getCompletedDate());

    }

    @Test
    public void testAddNotifyLocation() {
        MarkerLocation markerLocation = new MarkerLocation(UUID.randomUUID());
        markerLocation.setLongitude(12);
        markerLocation.setLatitude(24);
        TaskList list1 = this.repo.getAllLists().get(0);

        assertNull(list1.getRememberByDate());
        this.repo.addNotifyByLocation(list1.getUuid(), markerLocation);
        list1 = this.repo.getAllLists().get(0);
        assertNotNull(list1.getRememberByLocation());
        assertEquals(24, list1.getRememberByLocation().getLatitude(), 0.0001);
        assertEquals(12, list1.getRememberByLocation().getLongitude(), 0.0001);
    }


    @Test
    public void testRemoveNotifyLocation() {
        MarkerLocation markerLocation = new MarkerLocation(UUID.randomUUID());
        markerLocation.setLongitude(12);
        markerLocation.setLatitude(24);
        TaskList list1 = this.repo.getAllLists().get(0);

        assertNull(list1.getRememberByDate());
        this.repo.addNotifyByLocation(list1.getUuid(), markerLocation);
        list1 = this.repo.getAllLists().get(0);
        assertNotNull(list1.getRememberByLocation());
        assertEquals(24, list1.getRememberByLocation().getLatitude(), 0.0001);
        assertEquals(12, list1.getRememberByLocation().getLongitude(), 0.0001);
        this.repo.removeNotifyByLocation(list1.getUuid());
        list1 = this.repo.getAllLists().get(0);
        assertNull(list1.getRememberByDate());
    }

    @Test
    public void testAddSharedUser() {
        this.db.createUser("abc-123", "Max Muster");
        User user = this.db.getUser("abc-123");
        TaskList list1 = this.repo.getAllLists().get(0);
        assertNull(list1.getRememberByDate());
        this.repo.addUserToSharedList(list1.getUuid(), user.getSignature());
        list1 = this.repo.getAllLists().get(0);

        ArrayList<String> userList = new ArrayList<>(list1.getSharedUsers());
        assertEquals(1, userList.size());
    }

    @Test
    public void testRemoveSharedUser() {
        this.db.createUser("abc-123", "Max Muster");
        User user = this.db.getUser("abc-123");

        TaskList list1 = this.repo.getAllLists().get(0);
        this.repo.addUserToSharedList(list1.getUuid(), user.getSignature());
        list1 = this.repo.getAllLists().get(0);

        assertEquals(1, list1.getSharedUsers().size());

        ArrayList<String> userList = new ArrayList<>(list1.getSharedUsers());
        assertEquals(1, userList.size());
        this.repo.removeUserFromSharedList(list1.getUuid(), user.getSignature());
        list1 = this.repo.getAllLists().get(0);
        userList = new ArrayList<>(list1.getSharedUsers());
        assertEquals(0, userList.size());
    }

    @Test
    public void testIsShared() {
        this.db.createUser("abc-123", "Max Muster");
        User user = this.db.getUser("abc-123");
        TaskList list1 = this.repo.getAllLists().get(0);
        this.repo.addUserToSharedList(list1.getUuid(), user.getSignature());
        assertTrue(this.repo.isSharedList(list1.getUuid()));
        this.repo.removeUserFromSharedList(list1.getUuid(), user.getSignature());
        assertFalse(this.repo.isSharedList(list1.getUuid()));
    }


    @Test
    public void testAddNotifyByDate() {
        TaskList list1 = this.repo.getAllLists().get(0);

        assertNull(list1.getRememberByDate());
        this.repo.addNotifyByDate(list1.getUuid(), new Date());
        list1 = this.repo.getAllLists().get(0);
        assertNotNull(list1.getRememberByDate());
    }


    @Test
    public void testRemoveNotifyByDate() {
        TaskList list1 = this.repo.getAllLists().get(0);

        assertEquals(0, list1.getTasks().size());
        this.repo.addTask(list1.getUuid(), "TEst");
        list1 = this.repo.getAllLists().get(0);

        assertNull(list1.getRememberByDate());
        this.repo.addNotifyByDate(list1.getUuid(), new Date());
        list1 = this.repo.getAllLists().get(0);
        assertNotNull(list1.getRememberByDate());
        this.repo.removeNotifyByDate(list1.getUuid());
        list1 = this.repo.getAllLists().get(0);
        assertNull(list1.getRememberByDate());
    }

    @Test
    public void testDeleteTask() {
        TaskList list1 = this.repo.getAllLists().get(0);
        this.repo.addTask(list1.getUuid(), "TEst");
        Task task = this.repo.getAllTasks().get(0);
        assertEquals(1, this.repo.getAllTasks().size());
        this.repo.deleteTask(task.getUuid());
        assertEquals(0, this.repo.getAllTasks().size());
    }

    @Test
    public void testGetTasksForOneSpecificList(){
        TaskList list1 = this.repo.getAllLists().get(0);
        this.repo.addTask(list1.getUuid(), "Test-1");
        this.repo.addTask(list1.getUuid(), "Test-2");
        this.repo.addTask(list1.getUuid(), "Test-3");
        UUID listID = this.repo.getAllLists().get(0).getUuid();
        UUID secondTaskID = this.repo.getAllTasks().get(1).getUuid();
        this.repo.deleteTask(secondTaskID);
        assertEquals(2, this.repo.getAllTasksInList(listID).size());
        assertEquals("Test-1", this.repo.getAllTasksInList(listID).get(0).getDescription());
        assertEquals("Test-3", this.repo.getAllTasksInList(listID).get(1).getDescription());
        assertFalse(this.repo.getAllTasksInList(listID).get(0).isDeleted());
        assertFalse(this.repo.getAllTasksInList(listID).get(1).isDeleted());
    }


    @Test
    public void testGetTasksForOneSpecificListWithDeletedTasks(){
        TaskList list1 = this.repo.getAllLists().get(0);
        this.repo.addTask(list1.getUuid(), "Test-1");
        this.repo.addTask(list1.getUuid(), "Test-2");
        this.repo.addTask(list1.getUuid(), "Test-3");
        UUID listID = this.repo.getAllLists().get(0).getUuid();
        UUID secondTaskID = this.repo.getAllTasksInListWithDeleted(listID).get(1).getUuid();
        this.repo.deleteTask(secondTaskID);
        assertEquals(2, this.repo.getAllTasksInList(listID).size());
        assertEquals("Test-1", this.repo.getAllTasksInListWithDeleted(listID).get(0).getDescription());
        assertEquals("Test-2", this.repo.getAllTasksInListWithDeleted(listID).get(1).getDescription());
        assertEquals("Test-3", this.repo.getAllTasksInListWithDeleted(listID).get(2).getDescription());
        assertFalse(this.repo.getAllTasksInListWithDeleted(listID).get(0).isDeleted());
        assertTrue(this.repo.getAllTasksInListWithDeleted(listID).get(1).isDeleted());
        assertFalse(this.repo.getAllTasksInListWithDeleted(listID).get(2).isDeleted());
    }
}