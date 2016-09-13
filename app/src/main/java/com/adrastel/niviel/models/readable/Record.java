package com.adrastel.niviel.models.readable;

import android.os.Parcel;
import android.os.Parcelable;

import com.adrastel.niviel.models.BaseModel;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Record est une classe regroupant les records personnels d'un joueur
 *
 * Le joueur est identifié par son WCA ID
 *
 * Il possede des records en single ou en average
 */

public class Record extends BaseModel implements Parcelable {

    // Attributes


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


    protected Record(Parcel in) {
        event = in.readString();
        single = in.readString();
        nr_single = in.readString();
        cr_single = in.readString();
        wr_single = in.readString();
        average = in.readString();
        nr_average = in.readString();
        cr_average = in.readString();
        wr_average = in.readString();
        competitions = in.createTypedArrayList(History.CREATOR);
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    // Pour les logs
    @Override
    public String toString() {

        Gson gson = new Gson();
        return gson.toJson(this);

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

    public void setWr_average(String wr_average) {
        this.wr_average = wr_average;
    }

    public ArrayList<History> getCompetitions() {
        return competitions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(event);
        parcel.writeString(single);
        parcel.writeString(nr_single);
        parcel.writeString(cr_single);
        parcel.writeString(wr_single);
        parcel.writeString(average);
        parcel.writeString(nr_average);
        parcel.writeString(cr_average);
        parcel.writeString(wr_average);
        parcel.writeTypedList(competitions);
    }
}
