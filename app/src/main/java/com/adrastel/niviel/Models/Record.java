package com.adrastel.niviel.models;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Record est une classe regroupant les records personnels d'un joueur
 *
 * Le joueur est identifié par son WCA ID
 *
 * Il possede des records en single ou en average
 */

public class Record extends BaseModel implements Serializable {

    // Attributes

    /**
     * WCA ID, un ID unique
     */
    protected String wca_id;

    /**
     * Le type du cube
     */
    protected String event;

    /**
     * Le record du joueur en single en secondes
     */
    protected String single;

    /**
     * Le classement national du joueur en single
     */
    protected String nr_single;

    /**
     * Le classement continental du joueur en single
     */
    protected String cr_single;

    /**
     * Le classement mondial du joueur en signle
     */
    protected String wr_single;

    /**
     * Le record du joueur en average en secondes
     */
    protected String average;

    /**
     * Le classement national en average
     */
    protected String nr_average;

    /**
     * Le clasement continental du joueur en average
     */
    protected String cr_average;

    /**
     * Le classement mondial du joueur en average
     */
    protected String wr_average;

    /**
     * L'historique des competitions
     */
    protected ArrayList<History> competitions;

    // Constructors

    public Record() {}


    // Pour les logs
    @Override
    public String toString() {

        Gson gson = new Gson();
        return gson.toJson(this);

     }
    // Getters


    public String getWca_id() {
        return wca_id;
    }

    public String getEvent() {
        return event;
    }

    public String getSingle() {
        return single;
    }

    public String getNr_single() {
        return nr_single;
    }

    public String getCr_single() {
        return cr_single;
    }

    public String getWr_single() {
        return wr_single;
    }

    public String getAverage() {
        return average;
    }

    public String getNr_average() {
        return nr_average;
    }

    public String getCr_average() {
        return cr_average;
    }

    public String getWr_average() {
        return wr_average;
    }

    public ArrayList<History> getCompetitions() {
        return competitions;
    }
}
