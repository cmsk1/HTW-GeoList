package com.htwberlin.geolist.data.models;

public class User {
    private long id;
    private String signature;
    private String displayName;
    private long lastSync;

    public User(String signature) {
        this.signature = signature;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void updateLastSync() {
        this.lastSync = System.currentTimeMillis();
    }

    public String getSignature() {
        return this.signature;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getLastSync() {
        return this.lastSync;
    }

    public void setLastSync(long lastSync) {
        this.lastSync = lastSync;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User other = (User)obj;
            return this.signature.equals(other.signature);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.signature.hashCode();
    }
}
