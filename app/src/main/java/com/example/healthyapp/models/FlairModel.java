package com.example.healthyapp.models;

public class FlairModel {
    private int id;
    private String name;

    public FlairModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public FlairModel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}