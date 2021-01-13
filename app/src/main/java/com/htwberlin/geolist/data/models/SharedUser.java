package com.htwberlin.geolist.data.models;

public class SharedUser {
    private long id;
    private String signature;
    private long userId;
    private long listId;

    public SharedUser(String signature) {
        this.signature = signature;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSignature() {
        return signature;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SharedUser) {
            SharedUser other = (SharedUser)obj;
            return this.signature.equals(other.signature);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.signature.hashCode();
    }
}
