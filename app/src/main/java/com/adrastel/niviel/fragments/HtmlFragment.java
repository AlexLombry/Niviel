package com.adrastel.niviel.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.google.gson.Gson;

import java.util.ArrayList;

public abstract class HtmlFragment extends BaseFragment {

    protected SharedPreferences preferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SharedPreferences
        preferences = getContext().getSharedPreferences(Constants.SECRETS.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }


    /**
     * Sauvegarde les records
     * @param datas datas
     */
    protected void saveData(ArrayList datas, String storage) {
        Log.d("save");
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(datas);
        editor.putString(storage, json);
        editor.apply();
    }


}
