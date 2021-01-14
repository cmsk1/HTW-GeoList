package com.htwberlin.geolist.logic;

import com.htwberlin.geolist.data.models.Task;
import com.htwberlin.geolist.data.models.TaskList;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MergerTest {
    private static Task task1;
    private static Task task2;
    private static Task task3;
    private static Task task4V1;
    private static Task task4V2;
    private static Task task4V3;

    @BeforeAll
    public static void beforeAll() {
        UUID sharedUuid = UUID.randomUUID();
        long time = System.currentTimeMillis();
        task1 = new Task(UUID.randomUUID());
        task2 = new Task(UUID.randomUUID());
        task3 = new Task(UUID.randomUUID());
        task4V1 = new Task(sharedUuid);
        task4V2 = new Task(sharedUuid);
        task4V3 = new Task(sharedUuid);

        task1.setDescription("Random Desc1");
        task1.setCompleted(true);
        task1.setCreatedAt(new Date(0));
        task1.setChangeAt(new Date(10));
        task1.setCompletedDate(new Date(5));

        task2.setDescription("Random Desc2");
        task2.setCreatedAt(new Date(10));
        task2.setChangeAt(new Date(50));

        task3.setDeleted(true);
        task3.setDescription("Random Desc3");
        task3.setCreatedAt(new Date(2));
        task3.setChangeAt(new Date(2));

        task4V1.setDescription("Random Desc4 Version1");
        task4V1.setCreatedAt(new Date(0));
        task4V1.setChangeAt(new Date(10));
        task4V1.setCompletedDate(new Date(5));

        task4V2.setDescription("Random Desc4 Version2");
        task4V2.setCreatedAt(new Date(0));
        task4V2.setChangeAt(new Date(70));
        task4V2.setCompletedDate(new Date(50));

        task4V3.setDescription("Random Desc4 Version2");
        task4V3.setCreatedAt(new Date(0));
        task4V3.setChangeAt(new Date(10));
        task4V3.setDeleted(true);
    }

    @Test
    public void mergeWithDifferentProperties() {
        UUID sharedUuid = UUID.randomUUID();

        TaskList list1 = new TaskList(sharedUuid);
        list1.setDisplayName("Test1");
        list1.setCreatedAt(new Date(8));
        list1.setChangesAt(new Date(10));

        TaskList list2 = new TaskList(sharedUuid);
        list2.setDisplayName("Test2");
        list2.setCreatedAt(new Date(8));
        list2.setChangesAt(new Date(4));

        TaskListMerger merger = new TaskListMerger(list1, list2);
        TaskList merged = merger.merge();

        assertEquals("Test1", merged.getDisplayName());
        assertEquals(new Date(8), merged.getCreatedAt());
        assertEquals(new Date(10), merged.getChangesAt());
    }

    @Test
    public void mergeTasksWithinLists() {
        UUID sharedUuid = UUID.randomUUID();
        TaskList list1 = new TaskList(sharedUuid);
        list1.getTasks().add(task1);
        list1.getTasks().add(task4V1);

        TaskList list2 = new TaskList(sharedUuid);
        list2.getTasks().add(task2);
        list2.getTasks().add(task4V2);

        TaskListMerger merger = new TaskListMerger(list1, list2);
        TaskList merged = merger.merge();
        Collection<Task> mergedTasks = merged.getTasks();

        assertTrue(mergedTasks.contains(task1));
        assertTrue(mergedTasks.contains(task2));
        assertTrue(mergedTasks.contains(task4V2));
        assertEquals(3, mergedTasks.size());
    }

    @Test
    public void mergeTasksWithDeleted() {
        UUID sharedUuid = UUID.randomUUID();
        TaskList list1 = new TaskList(sharedUuid);
        list1.getTasks().add(task3);
        list1.getTasks().add(task4V1);

        TaskList list2 = new TaskList(sharedUuid);
        list2.getTasks().add(task2);
        list2.getTasks().add(task4V3);

        TaskListMerger merger = new TaskListMerger(list1, list2);
        TaskList merged = merger.merge();
        List<Task> mergedTasks = new ArrayList<>(merged.getTasks());
        Task deletedTask = mergedTasks.get(mergedTasks.indexOf(task4V3));

        assertTrue(mergedTasks.contains(task3));
        assertTrue(mergedTasks.contains(task4V3));
        assertTrue(mergedTasks.contains(task2));
        assertEquals(3, mergedTasks.size());

        assertEquals(true, deletedTask.isDeleted());
    }
}
