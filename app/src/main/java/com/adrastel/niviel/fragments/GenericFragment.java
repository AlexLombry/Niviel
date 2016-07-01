package com.adrastel.niviel.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.adrastel.niviel.Models.BaseModel;
import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.BaseAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Cette classe abstraite permet de creer facilement une liste
 * @param <M> le modele
 * @param <A> l'adapter
 */
public abstract class GenericFragment<M extends BaseModel, A extends BaseAdapter> extends HtmlFragment {

    SharedPreferences preferences;

    /**
     * Les données de la liste
     */
    private ArrayList<M> datas = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = Assets.getSharedPreferences(getContext());

    }

    /**
     * On récupère l'URL pour se connecter au site
     * @return url
     */
    protected abstract String getUrl();

    /**
     * On récupère l'adapter qui est initialisé et instancié dans la classe enfant
     * @return adapter
     */
    protected abstract A getAdapter();

    protected abstract String getStorage();

    /**
     * Getter des datas
     * @return datas
     */
    protected ArrayList<M> getDatas() {
        return datas;
    }

    /**
     * Callback
     */
    protected interface requestDataCallback {
        ArrayList<? extends BaseModel> parseDatas(Document document);

        void runOnUIThread(ArrayList<? extends BaseModel> datas);
    }

    /**
     * Fait une requete à un serveur, récupère le contenu, le parse et sort un objet qui est rafréchi avec la methode refreshData()
     * @param activity activité
     * @param callback callback
     */
    protected void requestData(final Activity activity, final requestDataCallback callback) {

        RequestQueue requestQueue = Volley.newRequestQueue(activity);


        StringRequest request = new StringRequest(Request.Method.GET, getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Document document = Jsoup.parse(response);

                        // On récupère l'objet en question
                        final ArrayList<M> datas = (ArrayList<M>) callback.parseDatas(document);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.runOnUIThread(datas);
                            }
                        });
                    }

                }).start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, activity.getString(R.string.error_connection), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);
    }

    /**
     * Met à jour la liste et le vue de la liste
     * @param datas données
     */
    protected void refreshData(ArrayList<M> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        getAdapter().notifyDataSetChanged();
    }

    protected void saveData(ArrayList<M> datas) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(datas);
        editor.putString(getStorage(), json);
        editor.apply();
        Log.d("save");
    }

    protected void refreshAndSaveData(ArrayList<M> datas) {
        refreshData(datas);
        saveData(datas);
    }
}
