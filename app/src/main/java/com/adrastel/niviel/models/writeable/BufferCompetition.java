package com.adrastel.niviel.models.writeable;

import com.adrastel.niviel.models.readable.Competition;

public class BufferCompetition extends Competition {

    public BufferCompetition() {
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setType(int type) {
        this.type = type;
    }
}
