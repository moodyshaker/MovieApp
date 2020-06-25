package com.example.MovieDB.model;

public class BSDObject {
    private String action;
    private int resource;
    private int color;

    public BSDObject(String action, int resource, int color) {
        this.action = action;
        this.resource = resource;
        this.color = color;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
