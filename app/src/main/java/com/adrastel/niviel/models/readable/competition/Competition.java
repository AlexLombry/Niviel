package com.adrastel.niviel.models.readable.competition;

import com.adrastel.niviel.models.BaseModel;

public class Competition extends BaseModel {


    protected String date;
    protected String competition;
    protected String competition_link;
    protected String country;
    protected String town;
    protected String place;
    protected String place_link;
    protected int type;

    public String getDate() {
        return date;
    }

    public String getCompetition() {
        return competition;
    }

    public String getCompetition_link() {
        return competition_link;
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

    public String getPlace_link() {
        return place_link;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "location='" + date + '\'' +
                ", competition='" + competition + '\'' +
                ", competition_link='" + competition_link + '\'' +
                ", country='" + country + '\'' +
                ", town='" + town + '\'' +
                ", place='" + place + '\'' +
                ", place_link='" + place_link + '\'' +
                ", type=" + type +
                '}';
    }
}
