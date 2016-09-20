package com.adrastel.niviel.providers.html;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.models.writeable.BufferRecord;
import com.adrastel.niviel.models.readable.History;
import com.adrastel.niviel.models.readable.Record;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Personal Record decode a partir du code HTML les record du joueur
 */
public class RecordProvider extends HtmlProvider {


    /**
     *
     * Recupere le document, extrait les records personnels et revoient l'objet R
     *
     * @param document le code serialisé
     * @return les records
     */
    public static ArrayList<Record> getRecord(final Context context, Document document, boolean addCompetitions) {

        ArrayList<Record> arrayList = new ArrayList<>();

        // On récupère la table

        Activity activity = null;

        try {
            activity = (Activity) context;
        }

        catch (ClassCastException e) {
            e.printStackTrace();
        }

        try {
            Element table = document.select("table").get(1);


            // On recupere les lignes
            Elements trs = table.select("tbody tr");

            BufferRecord bufferRecord;


            //Pour chaques lignes (on commance par la ligne 2 en partant de 0 puisque les 3 premières ligne sont inutiles

            for (int i = 2; i < trs.size(); i++) {

                // On recupere la ligne
                Element tr = trs.get(i);

                bufferRecord = hydrate(tr);


                /**
                 * Si on veux ajouter les competitions, on les recupere, on les filtres avec l'event et on les ajouter au buffer
                 */
                if (addCompetitions && activity != null) {

                    ArrayList<History> history = HistoryProvider.getHistory(activity, document, bufferRecord.getEvent());

                    bufferRecord.setCompetitions(history);


                }

                /**
                 * Si ce sont des entêtes, on les testes pour savoir si elles sont toujours les mêmes, pour verifier le code HTML
                 * Sinon on ajoute un record
                 */
                if (i == 2) {
                    if (!checkHTML(bufferRecord)) {
                        Toast.makeText(context, context.getString(R.string.error_interpretation_html), Toast.LENGTH_LONG).show();
                    }
                } else {
                    arrayList.add(bufferRecord);
                }
            }


        }

        catch (Exception exception) {

            if(activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                    }
                });
            }

        }

        return arrayList;
    }

    public static ArrayList<Record> getRecord(Context context, Document document) {
        return getRecord(context, document, false);
    }

    /**
     * Verifie le HTML avec les entetes
     * @param record l'element
     * @return true si c'est bon
     */
    private static boolean checkHTML(BufferRecord record) {

        return record.getEvent().equalsIgnoreCase("Event")
                && record.getNr_single().equalsIgnoreCase("NR")
                && record.getCr_single().equalsIgnoreCase("CR")
                && record.getWr_average().equalsIgnoreCase("WR")
                && record.getSingle().equalsIgnoreCase("Single")
                && record.getAverage().equalsIgnoreCase("Average")
                && record.getWr_average().equalsIgnoreCase("WR")
                && record.getCr_average().equalsIgnoreCase("CR")
                && record.getNr_average().equalsIgnoreCase("NR");
    }

    /**
     *
     * Hydrate l'objet avec le code Html
     *
     * @param bufferRecord buffer
     * @param tr la ligne
     * @return retounre le buffer
     */
    private static BufferRecord hydrate(BufferRecord bufferRecord, Element tr) {

        for(int j = 0; j < tr.children().size(); j++) {

            Element td = tr.children().get(j);

            switch (j) {

                case 0:
                    bufferRecord.setEvent(td.text());
                    break;

                case 1:
                    bufferRecord.setNr_single(td.text());
                    break;

                case 2:
                    bufferRecord.setCr_single(td.text());
                    break;

                case 3:
                    bufferRecord.setWr_single(td.text());
                    break;

                case 4:
                    bufferRecord.setSingle(td.text());
                    break;

                case 5:
                    bufferRecord.setAverage(td.text());
                    break;

                case 6:
                    bufferRecord.setWr_average(td.text());
                    break;

                case 7:
                    bufferRecord.setCr_average(td.text());
                    break;

                case 8:
                    bufferRecord.setNr_average(td.text());
                    break;
            }
        }

        return bufferRecord;

    }

    /**
     * On appelle hydrate en instanciant un nouveau bufferrecord
     * @param tr ligne
     * @return bufferrecord
     */
    private static BufferRecord hydrate(Element tr) {
        BufferRecord bufferRecord = new BufferRecord();

        return hydrate(bufferRecord, tr);
    }


    /**
     * Retourne une image en fonction d'un nombre
     * @param i type du cube
     * @return drawable
     */
    public static int getImage(int i) {

        String[] cubes = {"2x2 cube", "4x4 cube", "5x5 cube", "6x6 cube", "megaminx", "pyraminx", "square-1", "skewb"};

        int position = i % cubes.length;
        String event = cubes[position];

        return getImage(event);
    }

    /**
     * Retourne une image en fonction du type du cube
     *
     * @param event type du cube
     * @return drawable
     */
    public static int getImage(String event) {

        switch(event.toLowerCase()) {
            case "2x2 cube":
                return R.drawable.cube_2x2;

            case "4x4 cube":
                return R.drawable.cube_4x4;

            case "5x5 cube":
                return R.drawable.cube_5x5;

            case "6x6 cube":
                return R.drawable.cube_6x6;

            case "7x7 cube":
                return R.drawable.cube_7x7;

            case "megaminx":
                return R.drawable.megaminx;

            case "pyraminx":
                return R.drawable.piraminx;

            case "square-1":
                return R.drawable.square_1;

            case "rubik's clock":
                return R.drawable.cube_clock;

            case "skewb":
                return R.drawable.skewb;

            default:
                return R.drawable.cube_3x3;

        }
    }
}
