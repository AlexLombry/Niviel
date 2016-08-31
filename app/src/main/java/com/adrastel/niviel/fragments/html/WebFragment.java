package com.adrastel.niviel.fragments.html;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.models.readable.Record;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public abstract class WebFragment extends BaseFragment {

    protected SharedPreferences preferences;


    // Webdatas contient toutes les données necessaires pour faire fonctionner WebFragment
    public abstract Object[] getWebdatas();

    public static final int WEBDATAS_LENGTH = 2;

    public static final int STORAGE_LOCATION = 0;
    public static final int STORAGE_TYPE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    }

    protected ArrayList<Record> loadLocalData() {

        String json = preferences.getString((String) getWebdatas()[STORAGE_TYPE], null);

        return loadFromJson(json);
    }

    protected ArrayList<Record> loadLocalData(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            return savedInstanceState.getParcelableArrayList((String) getWebdatas()[STORAGE_TYPE]);
        }

        return new ArrayList<>();
    }

    protected void saveDatas(ArrayList<Record> records) {
        Gson gson = new Gson();

        String json = gson.toJson(records);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString((String) getWebdatas()[STORAGE_TYPE], json);
        editor.apply();
    }

    protected void saveDatas(Bundle savedInstanceState, ArrayList<Record> records) {
        savedInstanceState.putParcelableArrayList((String) getWebdatas()[STORAGE_TYPE], records);
    }

    protected ArrayList<Record> loadFromJson(String json) {
        if(json != null) {
            Gson gson = new Gson();
            return gson.fromJson(json, new TypeToken<ArrayList<Record>>(){}.getType());
        }

        else {
            return new ArrayList<>();
        }
    }

}
