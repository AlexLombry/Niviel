package com.adrastel.niviel.models.readable.competition;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

public class Title implements Parent<Competition> {

    private String title;
    private ArrayList<Competition> competitions;

    public Title(String title, ArrayList<Competition> competitions) {
        this.title = title;
        this.competitions = competitions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(ArrayList<Competition> competitions) {
        this.competitions = competitions;
    }

    @Override
    public List<Competition> getChildList() {
        return competitions;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
