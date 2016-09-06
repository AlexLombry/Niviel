package com.adrastel.niviel.models.readable;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("class")
    private String type;

    private String id;

    private String wca_id;

    private String name;

    @SerializedName("country_iso2")
    private String country;

    private String gender;

    private String created_at;

    private String url;

    public User() {

    }

    @Override
    public String toString() {
        return wca_id;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getWca_id() {
        return wca_id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getGender() {
        return gender;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }
}
