package com.adrastel.niviel.fragments.html;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.adrastel.niviel.R;
import com.adrastel.niviel.fragments.BaseFragment;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Cette classe abstraite permet de creer facilement une liste
 *
 * @param <M> le modele
 */
public abstract class HtmlFragment<M extends Parcelable> extends BaseFragment {

    protected SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                callData();
                return true;

            case R.id.goto_internet:
                //gotoInternet();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public abstract String getStorageLocation();
    public abstract Type getStorageType();
    public abstract void callData();

    protected ArrayList<M> loadLocalData() {

        String json = preferences.getString(getStorageLocation(), null);

        return loadFromJson(json);
    }

    protected ArrayList<M> loadLocalData(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            return savedInstanceState.getParcelableArrayList(getStorageLocation());
        }

        return new ArrayList<>();
    }

    protected void saveDatas(ArrayList<M> datas) {

        if(datas != null) {
            Gson gson = new Gson();

            String json = gson.toJson(datas);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getStorageLocation(), json);
            editor.apply();

        }
    }

    protected ArrayList<M> loadFromJson(String json) {
        if(json != null) {
            Gson gson = new Gson();
            return gson.fromJson(json, getStorageType());
        }

        else {
            return new ArrayList<>();
        }
    }

}
