package com.adrastel.niviel.assets;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;

import com.adrastel.niviel.R;

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

        return fromHtml(html);
    }


    public static @StringRes int onHttpError(int intStatusCode) {

        String statusCode = String.valueOf(intStatusCode);

        if(statusCode.length() == 3) {

            switch (statusCode.charAt(0)) {

                case '4':
                    return R.string.error_connection;

                case '5':
                    return R.string.err_server;


            }

        }

        return R.string.error_loading;

    }

}
