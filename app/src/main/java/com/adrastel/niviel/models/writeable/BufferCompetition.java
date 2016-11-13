package com.adrastel.niviel.models.writeable;

import com.adrastel.niviel.models.readable.Competition;

public class BufferCompetition extends Competition {


    public BufferCompetition() {
    }

    public BufferCompetition (String date, String competition, String competition_link, String country, String town, String place, String place_link, int type) {
        this.date = date;
        this.competition = competition;
        this.competition_link = competition_link;
        this.country = country;
        this.town = town;
        this.place = place;
        this.place_link = place_link;
        this.type = type;
    }


    public BufferCompetition setDate(String date) {
        this.date = date;
        return this;
    }

    public BufferCompetition setCompetition(String name) {
        this.competition = name;
        return this;
    }

    public BufferCompetition setCompetition_link(String competition_link) {
        this.competition_link = competition_link;
        return this;
    }

    public BufferCompetition setCountry(String country) {
        this.country = country;
        return this;
    }

    public BufferCompetition setTown(String town) {
        this.town = town;
        return this;
    }

    public BufferCompetition setPlace(String place) {
        this.place = place;
        return this;
    }

    public BufferCompetition setType(int type) {
        this.type = type;
        return this;
    }

    public BufferCompetition setPlace_link(String place_link) {
        this.place_link = place_link;
        return this;
    }
}
