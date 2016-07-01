package com.adrastel.niviel.Models;

import com.google.gson.Gson;

import java.io.Serializable;

public class History extends BaseModel implements Serializable{

    /**
     * Le type de cube
     */
    protected String event;

    /**
     * Le nom de la competition
     */
    protected String competition;

    /**
     * Le round (premier ou dexieme
     */
    protected String round;

    /**
     * Classement
     */
    protected String place;

    /**
     * Meilleur temps
     */
    protected String best;

    /**
     * temps en moyenne
     */
    protected String average;

    /**
     * tous les temps
     */
    protected String result_details;

    // Constructors

    /**
     * Contrcteur vide
     */
    public History() {}

    public History(History history) {
        event = history.getEvent();
        competition = history.getCompetition();
        round = history.getRound();
        place = history.getPlace();
        best = history.getBest();
        average = history.getAverage();
        result_details = history.getResult_details();
    }

    // Pour les logs
    @Override
    public String toString() {

        Gson gson = new Gson();
        return gson.toJson(this);

    }

    public String getEvent() {
        return event;
    }

    public String getCompetition() {
        return competition;
    }

    public String getRound() {
        return round;
    }

    public String getPlace() {
        return place;
    }

    public String getBest() {
        return best;
    }

    public String getAverage() {
        return average;
    }

    public String getResult_details() {
        return result_details;
    }


}
