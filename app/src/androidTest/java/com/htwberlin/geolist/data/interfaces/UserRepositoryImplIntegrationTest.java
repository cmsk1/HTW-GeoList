package com.htwberlin.geolist.data.interfaces;

import android.content.Context;

import com.htwberlin.geolist.data.models.User;
import com.htwberlin.geolist.data.sqlite.DatabaseHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserRepositoryImplIntegrationTest {
    DatabaseHelper db;
    UserRepositoryImpl repo;

    @Before
    public void setUp() {
        Context context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.db = new DatabaseHelper(context);
        this.repo = new UserRepositoryImpl(this.db);

        this.repo.addUser("abc-123", "Test");
        this.repo.addUser("xyz-123", "Mustermann");
    }

    @After
    public void tearDown() {
        this.db.clearAllTables();
        this.db.close();
    }

    @Test
    public void testUserRepoAddUserIsSuccessful() {
        User user = this.repo.getUser("abc-123");

        assertEquals("Test", user.getDisplayName());
        assertTrue(user.getId() > 0);
        User user2 = this.repo.getUser("xyz-123");

        assertEquals("Mustermann", user2.getDisplayName());
        assertTrue(user2.getId() > 0);
    }

    @Test
    public void testUserRepoGetUnknownUserIsNull() {
        User user = this.repo.getUser("123-456");
        assertNull(user);
    }


    @Test
    public void testUserRepoDeleteUserActuallyDeletesObject() {
        assertEquals(2, this.repo.getUsers().size());

        this.repo.deleteUser("abc-123");
        assertEquals(1, this.repo.getUsers().size());
    }

    @Test
    public void testUserRepoDeleteUserWithWrongUUIDDeletesNothing() {
        assertEquals(2, this.repo.getUsers().size());

        this.repo.deleteUser("asdasd");

        assertEquals(2, this.repo.getUsers().size());
    }

    @Test
    public void testIsKnownUser() {
        assertTrue(this.repo.isKnownUser("xyz-123"));
    }

    @Test
    public void testIsUnknownUser() {
        assertFalse(this.repo.isKnownUser("xyz-851"));
    }
}