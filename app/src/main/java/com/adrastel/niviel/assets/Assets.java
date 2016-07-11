package com.adrastel.niviel.assets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;

import com.adrastel.niviel.R;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.fragments.HistoryFragment;
import com.adrastel.niviel.fragments.RankingFragment;
import com.adrastel.niviel.fragments.RecordFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

public class Assets {

    public static boolean isConnected(@NonNull ConnectivityManager manager) {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }

    public static int random(int min, int max) {
        Random random = new Random();

        return random.nextInt(max - min + 1) + min;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.SECRETS.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void checkArrayList(ArrayList<?> arrayList) {
        if(arrayList != null) {

            if(arrayList.size() > 0) {
                Gson gson = new Gson();

                Log.d(gson.toJson(arrayList.get(0)));
            }

            else {
                Log.d("L'array est vide");
            }

        }

        else {
            Log.d("L'array est nul");
        }
    }

    public static String wrapStrong(String text) {
        return "<strong>" + text + "</strong>";
    }

    public static void shareIntent(Context context, String text, String html) {
        Intent intent = new Intent();

        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, html);

        Intent chooser = Intent.createChooser(intent, context.getString(R.string.share));

        context.startActivity(chooser);
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

    public static Spanned formatHtmlAverageDetails(String average, String details) {

        String html = "<strong>" + average + "</strong>" + " (" + details + ")";

        return fromHtml(html);
    }

    public static BaseFragment getFragmentFromId(@IdRes int id) {


        switch(id) {
            case R.id.nav_profile:
                return new RecordFragment();

            case R.id.nav_history:
                return new HistoryFragment();

            case R.id.nav_ranking:
                return new RankingFragment();

            default:
                return new RecordFragment();
        }
    }


}
