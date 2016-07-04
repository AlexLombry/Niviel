package com.adrastel.niviel.WCA;

import com.adrastel.niviel.Models.BufferRanking;
import com.adrastel.niviel.Models.Ranking;
import com.adrastel.niviel.assets.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class RankingProvider extends Provider {

    private static String rank_buffer = null;

    public static ArrayList<Ranking> getRanking(Document document) {

        ArrayList<Ranking> arrayList = new ArrayList<>();

        // On prend la table
        Element table = document.select("table").get(0);

        // On récupère les lignes
        Elements trs = table.select("tbody tr");

        BufferRanking bufferRanking;

        for(int i = 3; i < trs.size(); i++) {

            // On récupère la ligne
            Element tr = trs.get(i);

            bufferRanking = hydrate(tr);

            arrayList.add(bufferRanking);
            Log.d(String.valueOf(bufferRanking));
        }

        return arrayList;
    }

    private static BufferRanking hydrate(Element tr) {

        BufferRanking ranking = new BufferRanking();
        


        return hydrate(ranking, tr);
    }

    private static BufferRanking hydrate(BufferRanking bufferRanking, Element tr) {

        for(int i = 0; i < tr.children().size(); i++) {

            Element td = tr.children().get(i);
            String text = td.text();

            switch (i) {

                case 0:

                    // On regarde si le rank est vide ou non, si oui on insère l'ancien
                    if(text.replaceAll("\\s", "").length() == 0) {
                        bufferRanking.setRank(rank_buffer);
                    }

                    else {
                        bufferRanking.setRank(text);
                        rank_buffer = text;
                    }

                    break;

                case 1:
                    bufferRanking.setPerson(text);
                    break;

                case 2:
                    bufferRanking.setResult(text);
                    break;

                case 3:
                    bufferRanking.setCitizen(text);
                    break;

                case 4:
                    bufferRanking.setCompetition(text);
                    break;

            }

        }

        return bufferRanking;

    }

}
