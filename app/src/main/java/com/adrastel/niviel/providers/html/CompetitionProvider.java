package com.adrastel.niviel.providers.html;

import com.adrastel.niviel.models.readable.Competition;
import com.adrastel.niviel.models.writeable.BufferCompetition;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class CompetitionProvider {

    public static String IN_PROGRESS = "in-progress-comps";

    public static ArrayList<Competition> getCompetition(Document document, String type) {

        ArrayList<Competition> competitions = new ArrayList<>();

        try {

            Element table = document.getElementById(type);

            Elements lines = table.select("ul li");

            // On saute le titre
            for(int i = 1; i < lines.size(); i++) {

                Element line = lines.get(i);



            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return competitions;


    }

    private static BufferCompetition hydrate(Element line) {

        BufferCompetition competition = new BufferCompetition();

        return competition;
    }


}
