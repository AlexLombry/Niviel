package com.adrastel.niviel.models.writeable;

import android.os.Parcel;
import android.os.Parcelable;

import com.adrastel.niviel.models.BaseModel;

public class OldNewRecord extends BaseModel implements Parcelable {

    private String name;
    private String event;
    private String oldTime;
    private String newTime;
    private String oldNr;
    private String oldCr;
    private String oldWr;
    private String newNr;
    private String newCr;
    private String newWr;

    public OldNewRecord(String name, String event, String oldTime, String newTime, long oldNr, long oldCr, long oldWr, String newNr, String newCr, String newWr) {
        this.name = name;
        this.event = event;
        this.oldTime = oldTime;
        this.newTime = newTime;
        this.oldNr = String.valueOf(oldNr);
        this.oldCr = String.valueOf(oldCr);
        this.oldWr = String.valueOf(oldWr);
        this.newNr = newNr;
        this.newCr = newCr;
        this.newWr = newWr;
    }

    protected OldNewRecord(Parcel in) {
        name = in.readString();
        event = in.readString();
        oldTime = in.readString();
        newTime = in.readString();
        oldNr = in.readString();
        oldCr = in.readString();
        oldWr = in.readString();
        newNr = in.readString();
        newCr = in.readString();
        newWr = in.readString();
    }

    public static final Creator<OldNewRecord> CREATOR = new Creator<OldNewRecord>() {
        @Override
        public OldNewRecord createFromParcel(Parcel in) {
            return new OldNewRecord(in);
        }

        @Override
        public OldNewRecord[] newArray(int size) {
            return new OldNewRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(event);
        parcel.writeString(oldTime);
        parcel.writeString(newTime);
        parcel.writeString(oldNr);
        parcel.writeString(oldCr);
        parcel.writeString(oldWr);
        parcel.writeString(newNr);
        parcel.writeString(newCr);
        parcel.writeString(newWr);
    }

    public String getName() {
        return name;
    }

    public String getEvent() {
        return event;
    }

    public String getOldTime() {
        return oldTime;
    }

    public String getNewTime() {
        return newTime;
    }

    public String getOldNr() {
        return oldNr;
    }

    public String getOldCr() {
        return oldCr;
    }

    public String getOldWr() {
        return oldWr;
    }

    public String getNewNr() {
        return newNr;
    }

    public String getNewCr() {
        return newCr;
    }

    public String getNewWr() {
        return newWr;
    }
}
