package com.adrastel.niviel.fragments.html;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.models.BaseModel;
import com.adrastel.niviel.models.readable.Record;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_internet, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override
    public int getFabVisibility() {
        return View.VISIBLE;
    }

    @Override
    public int getFabIcon() {
        return R.drawable.ic_internet;
    }

    @Override
    public void onFabClick(View view) {
        //gotoInternet();
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

        else {
            Log.e("save datas null");
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
