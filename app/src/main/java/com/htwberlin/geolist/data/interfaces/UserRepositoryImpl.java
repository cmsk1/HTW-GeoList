package com.htwberlin.geolist.data.interfaces;

import com.htwberlin.geolist.data.models.User;
import com.htwberlin.geolist.data.sqlite.DatabaseHelper;

import java.util.Collection;

public class UserRepositoryImpl implements UserRepository {
    private final DatabaseHelper db;

    public UserRepositoryImpl(DatabaseHelper db) {
        this.db = db;
    }

    @Override
    public void addUser(String signature, String displayName) {
        if (signature == null)
            throw new IllegalArgumentException();

        this.db.createUser(signature, displayName);
    }

    @Override
    public void deleteUser(String signature) {
        if (signature == null)
            throw new IllegalArgumentException();
        User user  = this.getUser(signature);

        if (user != null && user.getId() > 0)
            this.db.deleteUser(user.getId());
    }

    @Override
    public boolean isKnownUser(String signature) {
        if (signature == null)
            return false;
        User user  = this.getUser(signature);

        return user != null;
    }

    @Override
    public User getUser(String signature) {
        if (signature == null)
            throw new IllegalArgumentException();

        return this.db.getUser(signature);
    }

    @Override
    public Collection<User> getUsers() {
        return this.db.getAllUsers();
    }
}
