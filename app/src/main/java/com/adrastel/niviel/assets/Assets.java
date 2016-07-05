package com.adrastel.niviel.assets;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

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

}
