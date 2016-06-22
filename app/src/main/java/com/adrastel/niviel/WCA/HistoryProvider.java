package com.adrastel.niviel.WCA;

import android.content.Context;
import android.widget.Toast;

import com.adrastel.niviel.Models.BufferHistory;
import com.adrastel.niviel.Models.History;
import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * La classe est globalement statique
 *
 * Elle récupere le code html, récupere les compet, et hydrate le record des competitions
 */
public class HistoryProvider {

    private static final String event_class = "caption";
    private static final String event_colspan_attr = "colspan";
    private static final String event_colspan = "8";


    /**
     * La fonction getAllHistory recupère toutes les competitions à l'aide du code HTML
     * @param context context
     * @param document document
     * @param filter filter
     * @return competitions
     */
    public static ArrayList<History> getHistory(Context context, Document document, String filter) {


        ArrayList<History> arrayList = new ArrayList<>();

        // On récupère la table
        Element table = document.select("table").last();

        // On récupère les lignes
        Elements trs = table.select("tbody tr");

        String event = null;


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

            if(isEvent(tr)) {
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

                    if (!checkHtml(hydrate(tr))) {
                        Toast.makeText(context, context.getString(R.string.error_interpretation_html), Toast.LENGTH_LONG).show();
                        Log.e("Error HTML history");
                    }
                }

                /**
                 * Sinon si c'est une compet
                 */
                else if (isCompetition(tr)) {


                    if(filter != null) {

                        if(filter.equalsIgnoreCase(bufferHistory.getEvent())) {
                            arrayList.add(hydrate(tr, bufferHistory));
                        }

                    }

                    else {
                        arrayList.add(hydrate(tr, bufferHistory));

                    }


                }

            }

        }

        return arrayList;

    }

    /**
     * Execute getHistory mais récupere les historique sans filtre
     *
     * @see HistoryProvider#getHistory(Context, Document)
     * @param context context
     * @param document document
     * @return arraylist
     */
    public static ArrayList<History> getHistory(Context context, Document document) {
        return getHistory(context, document, null);
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

    //todo resourdre le pb des competitions vides
    /**
     *
     * On donne une ligne brut, et hydrate dispatch les element et les rassemble dans un bufferhidtory
     *
     * On cree un nouveau buffer et on ajoute des element de l'ancien buffer
     *
     * @see BufferHistory
     *
     * @param tr la ligne
     * @param oldBuffer l'ancien buffer
     * @return un tempon de history
     */
    private static BufferHistory hydrate(Element tr, BufferHistory oldBuffer) {

        BufferHistory bufferHistory = new BufferHistory();





        for(int j = 0; j < tr.children().size(); j++) {

            String text = tr.child(j).text();

            switch (j) {
                // Si la competition est vide, on récupere celle précédente
                case 0:

                    // On regarde si le texte est vide en comptant les espaces
                    if(text.replaceAll("\\s", "").length() == 0 && oldBuffer != null) {
                        bufferHistory.setCompetition(oldBuffer.getCompetition());
                    }

                    else {
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

        // On ajoute dans le nouveau buffer des attributs de l'ancien afin de les concerver

        String event = oldBuffer != null ? oldBuffer.getEvent() : null;

        bufferHistory.setEvent(event);

        return bufferHistory;
    }

    private static BufferHistory hydrate(Element tr) {
        return hydrate(tr, null);
    }



}
