package com.htwberlin.geolist.data.interfaces;

import com.htwberlin.geolist.data.models.User;

import java.util.Collection;

public interface UserRepository {
    void addUser(String signature, String displayName);

    void deleteUser(String signature);

    boolean isKnownUser(String signature);

    User getUser(String signature);

    Collection<User> getUsers();
}
