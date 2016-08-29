package com.adrastel.niviel.fragments.html;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.BaseAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.models.BaseModel;
import com.adrastel.niviel.http.EncodedRequest;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Cette classe abstraite permet de creer facilement une liste
 *
 * @param <M> le modele
 * @param <A> l'adapter
 */
public abstract class HtmlFragment<M extends BaseModel, A extends BaseAdapter> extends BaseFragment {

    protected SharedPreferences preferences;

    /**
     * Les données de la liste
     */
    private ArrayList<M> datas = new ArrayList<>();
    private EncodedRequest request;

    ConnectivityManager connectivityManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        preferences = Assets.getSharedPreferences(getContext());

        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        getAdapter().setActivity(getActivity());

    }

    @Override
    public void onPause() {
        super.onPause();

        if(request != null) {
            request.cancel();
        }
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
                refreshData(datas);
                return true;

            case R.id.goto_internet:
                gotoInternet();
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
        gotoInternet();
    }

    /**
     * Getter des datas
     *
     * @return datas
     */
    protected ArrayList<M> getDatas() {
        return datas;
    }

    /**
     * Ouvre la page internet vers la WCA
     */
    protected void gotoInternet() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getUrl()));
        startActivity(Intent.createChooser(intent, getString(R.string.open_with)));
    }

    /**
     * Fait une requete à un serveur, récupère le contenu, le parse et sort un objet qui est rafréchi avec la methode refreshData()
     *
     * @param activity activité
     * @param callback callback
     */
    protected void requestData(final Activity activity, final requestDataCallback callback) {
        // todo: passer en okhttp
        RequestQueue requestQueue = Volley.newRequestQueue(activity);


        //StringRequest request = new StringRequest(Request.Method.GET, getUrl(), new Response.Listener<String>() {
        request = new EncodedRequest(activity, Request.Method.GET, getUrl(), new Response.Listener<String>() {
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
                                callback.onSuccess(datas);
                                callback.postRequest();
                            }
                        });
                    }

                }).start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, R.string.error_connection, Toast.LENGTH_LONG).show();
                callback.onError(error);
                callback.postRequest();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    /**
     * On récupère l'URL pour se connecter au site
     *
     * @return url
     */
    protected abstract String getUrl();

    /**
     * Raffréchie et sauvegarde
     *
     * @param datas données
     */
    protected void refreshAndSaveData(ArrayList<M> datas) {
        refreshData(datas);
        saveData(datas);
    }

    /**
     * Met à jour la liste et le vue de la liste
     *
     * @param datas données
     */
    protected void refreshData(ArrayList<M> datas) {

        if(isConnected()) {

            this.datas.clear();
            this.datas.addAll(datas);

            getAdapter().notifyDataSetChanged();

        }

        else {
            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sauvegarde les données
     *
     * @param datas données
     */
    protected void saveData(ArrayList<M> datas) {
        SharedPreferences.Editor editor = preferences.edit();

        Log.d(getStorage());

        editor.putString(getStorage(), datasToString(datas));
        editor.apply();
        Log.d("save");
    }

    /**
     * On récupère l'adapter qui est initialisé et instancié dans la classe enfant
     *
     * @return adapter
     */
    protected abstract A getAdapter();

    protected abstract String getStorage();

    /**
     * Transforme les donnees en String
     *
     * @param datas données
     * @return données en string
     */
    private String datasToString(ArrayList<M> datas) {
        Gson gson = new Gson();
        return gson.toJson(datas);
    }


    protected interface loadLocalDataCallback {
        Type getType();
    }

    /**
     * Récupère les données en local
     * @param callback type
     */
    protected void loadLocalData(loadLocalDataCallback callback) {

        String json = preferences.getString(getStorage(), null);

        // Si on a déja sauvegardé une copie, on charge la copie
        if(json != null) {

            Gson gson = new Gson();
            ArrayList<M> datas;


            // Tokentype recupere le type de l'objet puisque ArrayList est générique
            Type type = callback.getType();

            // On recupere le record en deserialisant l'objet
            datas = gson.fromJson(json, type);

            // On ajoute les resultats et on les affiche
            refreshData(datas);

        }
    }

    /**
     * Transforme les données en array
     *
     * @param datas donnees en string
     * @return données en array
     */
    protected ArrayList<M> datasToArray(String datas) {
        Type type = new TypeToken<ArrayList<M>>() {}.getType();
        Gson gson = new Gson();

        return gson.fromJson(datas, type);
    }

    /**
     * HttpCallback
     */
    protected interface requestDataCallback {
        ArrayList<? extends BaseModel> parseDatas(Document document);

        void onSuccess(ArrayList<? extends BaseModel> datas);
        void onError(VolleyError error);
        void postRequest();
    }
}
