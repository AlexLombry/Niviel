package com.adrastel.niviel.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adrastel.niviel.Models.History;
import com.adrastel.niviel.R;
import com.adrastel.niviel.WCA.HistoryProvider;
import com.adrastel.niviel.adapters.HistoryAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryFragment extends HtmlFragment {

    private Activity activity;
    private HistoryAdapter adapter;
    private SharedPreferences preferences;
    private ConnectivityManager connectivityManager;
    private ArrayList<History> histories = new ArrayList<>();
    private RequestQueue requestQueue;

    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        requestQueue = Volley.newRequestQueue(activity);
        preferences = activity.getSharedPreferences(Constants.SECRETS.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        adapter = new HistoryAdapter(histories);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_list_recycler);
        assert recyclerView != null;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        recyclerView.setAdapter(adapter);

        progressBar = (ProgressBar) view.findViewById(R.id.fragment_list_loader);

        progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // On charge les données si elles sont dans la RAM
        if(savedInstanceState != null && savedInstanceState.getString(Constants.STORAGE.HISTORY, null) != null) {


            String json = savedInstanceState.getString(Constants.STORAGE.HISTORY, null);

            if(json != null) {
                Type type = new TypeToken<ArrayList<History>>() {}.getType();
                Gson gson = new Gson();

                ArrayList<History> histories = gson.fromJson(json, type);

                refreshData(histories);

            }

            progressBar.setVisibility(View.GONE);
        }

        // Sinon si on est connecté à internet, on recupère les données depuis internet
        else if(Assets.isConnected(connectivityManager)) {
            requestData();
        }

        // Sinon on charge les données depuis la mémoire de l'appareil
        else {
            loadLocalData();
            progressBar.setVisibility(View.GONE);
            Toast.makeText(activity, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sauvegarde les records (lors de la rotation de l'ecran par exemple
     * @param outState bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // todo: mettre en parcelable
        Gson gson = new Gson();
        String json = gson.toJson(histories);

        outState.putString(Constants.STORAGE.HISTORY, json);
    }

    @Override
    public String getTitle() {
        return activity != null ? activity.getString(R.string.title_activity_history) : null;
    }


    private void requestData() {

        StringRequest request = new StringRequest(Request.Method.GET, RecordFragment.getStaticUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {


                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        // On parse le document et recupere les records que l'on met dans un adapter
                        Document document = Jsoup.parse(response);

                        // On remplace les anciennes données par des nouvelles
                        final ArrayList<History> histories = HistoryProvider.getHistory(activity, document);

                        Gson gson = new Gson();
                        Log.d(gson.toJson(histories));

                        // todo ne pas oublier d'ajouter le record pour faire des comparaisons
                        //HistoryProvider.getHistory(activity, document);

                        // On affiche les données et on coupe le chargement sur l'UI Thread

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshData(histories);
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        // On sauvegarde le record en JSON via Gson
                        saveData(histories, Constants.STORAGE.HISTORY);

                    }
                }).start();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(activity, getString(R.string.error_connection), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);

    }

    /**
     * Actualise l'historique
     * @param histories historique
     */
    private void refreshData(ArrayList<History> histories) {
        this.histories.clear();
        this.histories.addAll(histories);
        adapter.notifyDataSetChanged();
    }


    private boolean loadLocalData() {

        String json = preferences.getString(Constants.STORAGE.HISTORY, null);

        // Si on a déja sauvegardé une copie, on charge la copie
        if(json != null) {
            Gson gson = new Gson();
            ArrayList<History> histories;


            // Tokentype recupere le type de l'objet puisque ArrayList est générique
            Type type = new TypeToken<ArrayList<History>>() {}.getType();

            // On recupere le record en deserialisant l'objet
            histories = gson.fromJson(json, type);

            // On ajoute les resultats et on les affiche
            refreshData(histories);

            return true;
        }

        return false;
    }
}
