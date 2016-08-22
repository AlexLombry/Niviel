package com.adrastel.niviel.database;


public class Follower {

    private Long id;

    private String name;

    private String wca_id;

    private long addedDate;

    public Follower(String name, String wca_id, long addedDate) {
        this.name = name;
        this.wca_id = wca_id;
        this.addedDate = addedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWca_id() {
        return wca_id;
    }

    public void setWca_id(String wca_id) {
        this.wca_id = wca_id;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }
}
