package com.htwberlin.geolist.data;

import com.htwberlin.geolist.data.interfaces.UserRepository;
import com.htwberlin.geolist.data.models.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NullUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public void addUser(String signature, String displayName) {
        User user = new User(signature);
        user.setDisplayName(displayName);
        this.users.put(signature, user);
    }

    @Override
    public void deleteUser(String signature) {
        this.users.remove(signature);
    }

    @Override
    public boolean isKnownUser(String signature) {
        return this.users.containsKey(signature);
    }

    @Override
    public User getUser(String signature) {
        return this.users.get(signature);
    }

    @Override
    public Collection<User> getUsers() {
        return this.users.values();
    }
}
