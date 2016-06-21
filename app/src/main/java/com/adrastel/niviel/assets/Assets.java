package com.adrastel.niviel.assets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Assets {

    public static boolean isConnected(ConnectivityManager manager) {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }


}
