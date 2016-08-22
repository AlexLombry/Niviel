package com.adrastel.niviel.providers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.models.writeable.BufferHistory;
import com.adrastel.niviel.models.readable.History;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * La classe est globalement statique
 *
 * Elle récupere le code html, récupere les compet, et hydrate le record des competitions
 */
public class HistoryProvider extends Provider{

    private static final String event_class = "caption";
    private static final String event_colspan_attr = "colspan";
    private static final String event_colspan = "8";

    private static String event_buffer = null;
    private static String competition_buffer = null;


    /**
     * La fonction getAllHistory recupère toutes les competitions à l'aide du code HTML
     * @param activity context
     * @param document document
     * @param filter filter
     * @return competitions
     */
    public static ArrayList<History> getHistory(final Activity activity, Document document, String filter) {


        ArrayList<History> arrayList = new ArrayList<>();


        try {

            // On récupère la table
            Element table = document.select("table").last();

            // On récupère les lignes
            Elements trs = table.select("tbody tr");


            /**
             *
             * On initialise un buffer qui contient les variables temporairement
             *
             */

            BufferHistory bufferHistory = new BufferHistory();

            for (int i = 3; i < trs.size(); i++) {

                Element tr = trs.get(i);


                /**
                 * On regarde si c'est un titre
                 */
            /*if (isEvent(tr) && tr.text() != null && tr.text().replaceAll("\\s", "").length() != 0) {
                bufferHistory.setEvent(tr.text());
            }*/

                if (isEvent(tr)) {
                    event_buffer = tr.text();
                    bufferHistory.setEvent(tr.text());
                }

                /**
                 * Sinon
                 */
                else {

                    /**
                     * On verifie le HTML si c'est un sous titre
                     */
                    if (isTitle(tr)) {
                        //Log.d("mock test");
                        if (!checkHtml(hydrate(tr))) {
                            Toast.makeText(activity, activity.getString(R.string.error_interpretation_html), Toast.LENGTH_LONG).show();
                            Log.e("Error HTML history");
                        }
                    }

                    /**
                     * Sinon si c'est une compet
                     */
                    else if (isCompetition(tr)) {


                        if (filter != null) {

                            if (filter.equalsIgnoreCase(bufferHistory.getEvent())) {
                                arrayList.add(hydrate(tr));
                            }

                        } else {
                            arrayList.add(hydrate(tr));

                        }


                    }

                }

            }
        }

        catch (Exception exception) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, activity.getString(R.string.error_loading), Toast.LENGTH_LONG).show();
                }
            });
        }

        return arrayList;

    }

    public static ArrayList<History> getHistory(Activity activity, Document document) {
        return getHistory(activity, document, null);
    }

    /**
     *
     * Une ligne caractérisant un event est caractérisé par un td de classe caption et de colspan 8 ayant un lien de classe e
     *
     * On récupere donc ces element
     *
     * @param tr la ligne
     * @return true si c'est un rubik's cube
     */
    private static boolean isEvent(Element tr) {

        Element td = tr.children().first();

        return td.hasClass(event_class) && td.hasAttr(event_colspan_attr) && td.attr(event_colspan_attr).equals(event_colspan);

    }

    /**
     *
     * Un ligne caractérisant une compt comporte 8 enfants (y'a des blancs) tous ses element sont des td (on test donc le premier)
     *
     * @param tr la ligne
     * @return true si c'est une competition
     */
    private static boolean isCompetition(Element tr) {

        Elements children = tr.children();

        return children.size() == 8 && children.first().tagName().equals("td");

    }

    /**
     *
     * Un ligne caractérisant un titre comporte 8 enfants (y'a des blancs) tous ses element sont des th (on test donc le premier)
     *
     * @param tr la ligne
     * @return true si c'est une competition
     */
    private static boolean isTitle(Element tr) {

        Elements children = tr.children();

        return children.size() == 8 && children.first().tagName().equals("th");

    }

    private static boolean checkHtml(BufferHistory buffer) {

        return buffer.getCompetition().equalsIgnoreCase("competition")
                && buffer.getRound().equalsIgnoreCase("round")
                && buffer.getPlace().equalsIgnoreCase("place")
                && buffer.getBest().equalsIgnoreCase("best")
                && buffer.getAverage().equalsIgnoreCase("average")
                && buffer.getResult_details().equalsIgnoreCase("result details");

    }

    /**
     *
     * On donne une ligne brut, et hydrate dispatch les element et les rassemble dans un bufferhidtory
     *
     * On cree un nouveau buffer et on ajoute des element de l'ancien buffer
     *
     * @see BufferHistory
     *
     * @param tr la ligne
     * @return un tempon de history
     */
    private static BufferHistory hydrate(Element tr) {

        BufferHistory bufferHistory = new BufferHistory();


        for(int j = 0; j < tr.children().size(); j++) {

            String text = tr.child(j).text();


            switch (j) {
                // Si la competition est vide, on récupere celle précédente
                case 0:

                    // On regarde si le texte est vide en comptant les espaces
                    if(text.replaceAll("\\s", "").length() == 0) {
                        bufferHistory.setCompetition(competition_buffer);
                    }

                    else {
                        competition_buffer = text;
                        bufferHistory.setCompetition(text);
                    }
                     break;
                case 1: bufferHistory.setRound(text); break;
                case 2: bufferHistory.setPlace(text); break;
                case 3: bufferHistory.setBest(text); break;
                // case 4: un blanc
                case 5: bufferHistory.setAverage(text); break;
                // case 6: un blanc
                case 7: bufferHistory.setResult_details(text); break;
            }
        }

        bufferHistory.setEvent(event_buffer);

        return bufferHistory;
    }



}
