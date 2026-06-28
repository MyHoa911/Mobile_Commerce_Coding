package com.lethimyhoa.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private String username;
    private String password;
    private String role;
    private boolean isSaved;

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserAccount() {
    }
    //constructor
    public UserAccount(String username, String password, String role, String displayName, boolean isSaved) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
        this.isSaved = isSaved;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    @NonNull
    @Override
    public String toString() {
        return this.username;
    }
}
