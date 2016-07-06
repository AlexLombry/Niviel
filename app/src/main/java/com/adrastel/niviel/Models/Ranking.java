package com.adrastel.niviel.models;

import com.google.gson.Gson;

public class Ranking extends BaseModel {

    protected String rank;
    protected String person;
    protected String result;
    protected String citizen;
    protected String competition;

    public Ranking() {};

    @Override
    public String toString() {

        Gson gson = new Gson();
        return gson.toJson(this);

    }

    public String getRank() {
        return rank;
    }


    public String getPerson() {
        return person;
    }

    public String getResult() {
        return result;
    }

    public String getCitizen() {
        return citizen;
    }

    public String getCompetition() {
        return competition;
    }


}
