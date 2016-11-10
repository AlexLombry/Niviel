package com.adrastel.niviel.models.readable;

import android.os.Parcel;
import android.os.Parcelable;

import com.adrastel.niviel.models.BaseModel;
import com.google.gson.Gson;

/**
 * Record est une classe regroupant les records personnels d'un joueur
 *
 * Le joueur est identifié par son WCA ID
 *
 * Il possede des records en single ou en average
 */

public class Record extends BaseModel implements Parcelable {

    public static class Comparator implements java.util.Comparator<Record> {

        @Override
        public int compare(Record record, Record t1) {
            return record.getEvent().compareTo(t1.getEvent());
        }
    }

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

    public void setEvent(String event) {
        this.event = event;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public void setNr_single(String nr_single) {
        this.nr_single = nr_single;
    }

    public void setCr_single(String cr_single) {
        this.cr_single = cr_single;
    }

    public void setWr_single(String wr_single) {
        this.wr_single = wr_single;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public void setNr_average(String nr_average) {
        this.nr_average = nr_average;
    }

    public void setCr_average(String cr_average) {
        this.cr_average = cr_average;
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
    }
}
