package com.adrastel.niviel.models.readable;

import com.adrastel.niviel.models.BaseModel;

public class Competition extends BaseModel {


    protected String date;
    protected String name;
    protected String country;
    protected String town;
    protected String place;
    protected int type;

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getTown() {
        return town;
    }

    public String getPlace() {
        return place;
    }
}
