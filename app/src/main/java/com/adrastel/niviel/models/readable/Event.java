package com.adrastel.niviel.models.readable;

import android.os.Parcel;
import android.os.Parcelable;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

public class Event implements ParentListItem, Parcelable {

    private String title;
    private ArrayList<History> histories;

    public Event(String title, ArrayList<History> histories) {
        this.title = title;
        this.histories = histories;
    }

    protected Event(Parcel in) {
        title = in.readString();
        histories = in.createTypedArrayList(History.CREATOR);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public List<?> getChildItemList() {
        return histories;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeTypedList(histories);
    }
}