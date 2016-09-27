package com.adrastel.niviel.assets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;

import com.adrastel.niviel.R;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.database.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Assets {

    public static boolean isConnected(@NonNull ConnectivityManager manager) {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String wrapStrong(String text) {
        return "<strong>" + text + "</strong>";
    }

    /**
     * Retourne le tag qui permet d'avoir l'url
     * https://www.worldcubeassociation.org/results/events.php
     * @param position position dans la liste
     * @return tag
     */
    public static String getCubeId(int position) {

        switch (position) {
            case 1:
                return "444";

            case 2:
                return "555";

            case 3:
                return "222";

            case 4:
                return "333bf";

            case 5:
                return "333oh";

            case 6:
                return "333fm";

            case 7:
                return "333ft";

            case 8:
                return "minx";

            case 9:
                return "pyram";

            case 10:
                return "sq1";

            case 11:
                return "clock";

            case 12:
                return "skewb";

            case 13:
                return "666";

            case 14:
                return "777";

            case 15:
                return "444bf";

            case 16:
                return "555bf";

            case 17:
                return "333mbf";

            default:
                return "333";
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        }

        else {
            return Html.fromHtml(html);
        }
    }

    @SuppressWarnings("deprecation")
    public static int getColor(Context context, int id) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static Spanned formatHtmlAverageDetails(String average, String details) {

        String html = "<strong>" + average + "</strong>" + " (" + details + ")";
        html = html.replace("DNF", "<font color=\"#CC0000\">DNF</font>");
        html = html.replace("DNS", "<font color=\"#FF8800\">DNS</font>");
        html = html.trim();
        return fromHtml(html);
    }

    public static Spanned formatHtmltitle(String event, String competition) {

        String html = "<i>" + event + "</i>: " + competition;

        return fromHtml(html);

    }


    public static boolean isFollowing(Context context, String wca_id) {

        DatabaseHelper db = DatabaseHelper.getInstance(context);

        ArrayList<Follower> followers = db.selectAllFollowers();

        for(Follower follower : followers) {
            if(follower.wca_id().equals(wca_id)) {
                return true;
            }
        }

        return false;

    }

    @SuppressWarnings("StringEquality")
    public static boolean isPersonal(Context context, String wca_id) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String pers_wca_id = preferences.getString(context.getString(R.string.pref_wca_id), null);

        return wca_id == pers_wca_id;
    }

    /**
     * Compare les records et si il y a un nouveau record, l'ajoute dans un ArrayList
     * La taille d'oldRecord est toujours inférieure ou égale à celle des newRecords
     *
     * @param oldRecords anciens records
     * @param newRecords nouveaux records
     * @return nouvels events
     */
    public static ArrayList<com.adrastel.niviel.models.readable.Record> getNewRecords(ArrayList<Record> oldRecords, ArrayList<com.adrastel.niviel.models.readable.Record> newRecords) {

        ArrayList<com.adrastel.niviel.models.readable.Record> newEvents = new ArrayList<>();

        // Indice utilisé pour se décaler
        int i = 0;

        try {

            for(com.adrastel.niviel.models.readable.Record newRecord : newRecords) {

                Record oldRecord = oldRecords.get(i);

                if(!oldRecord.event().equals(newRecord.getEvent())) {

                    newEvents.add(newRecord);

                    // Reduit le rang pour ne pas se décaler par rapport à la nouvelle suite
                    i--;
                }


                i++;

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return newEvents;

    }

}
