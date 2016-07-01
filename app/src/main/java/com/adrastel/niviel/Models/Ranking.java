package com.adrastel.niviel.Models;

import com.google.gson.Gson;

public class Ranking extends BaseModel {

    private String person;
    private String result;
    private String citizen;
    private String competition;

    public Ranking() {};

    @Override
    public String toString() {

        Gson gson = new Gson();
        return gson.toJson(this);

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
