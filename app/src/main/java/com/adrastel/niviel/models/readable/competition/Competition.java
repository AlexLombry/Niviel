package com.adrastel.niviel.models.readable.competition;

import android.os.Parcel;
import android.os.Parcelable;

import com.adrastel.niviel.models.BaseModel;

public class Competition extends BaseModel implements Parcelable {


    protected String date;
    protected String competition;
    protected String competition_link;
    protected String country;
    protected String town;
    protected String place;
    protected String place_link;
    protected int type;

    public Competition() {

    }

    protected Competition(Parcel in) {
        date = in.readString();
        competition = in.readString();
        competition_link = in.readString();
        country = in.readString();
        town = in.readString();
        place = in.readString();
        place_link = in.readString();
        type = in.readInt();
    }

    public static final Creator<Competition> CREATOR = new Creator<Competition>() {
        @Override
        public Competition createFromParcel(Parcel in) {
            return new Competition(in);
        }

        @Override
        public Competition[] newArray(int size) {
            return new Competition[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(competition);
        parcel.writeString(competition_link);
        parcel.writeString(country);
        parcel.writeString(town);
        parcel.writeString(place);
        parcel.writeString(place_link);
        parcel.writeInt(type);
    }
}
