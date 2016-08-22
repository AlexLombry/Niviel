package com.adrastel.niviel.models.readable;

import com.adrastel.niviel.models.BaseModel;
import com.google.gson.Gson;

public class Profile extends BaseModel {

    private String url;
    private String id;
    private String wca_id;
    private String name;
    private String gender;
    private String country_iso2;
    private String created_at;
    private String updated_at;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWca_id() {
        return wca_id;
    }

    public void setWca_id(String wca_id) {
        this.wca_id = wca_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry_iso2() {
        return country_iso2;
    }

    public void setCountry_iso2(String country_iso2) {
        this.country_iso2 = country_iso2;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
