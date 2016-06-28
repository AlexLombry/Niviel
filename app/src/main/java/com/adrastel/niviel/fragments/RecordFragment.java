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

import com.adrastel.niviel.Models.Record;
import com.adrastel.niviel.R;
import com.adrastel.niviel.WCA.RecordProvider;
import com.adrastel.niviel.adapters.RecordAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.android.volley.DefaultRetryPolicy;
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

public class RecordFragment extends BaseFragment {

    // Composants
    private Activity activity;
    private RequestQueue requestQueue;
    private SharedPreferences preferences;
    private ConnectivityManager connectivityManager;

    // Adapters
    private RecordAdapter adapter;

    // Collections
    private ArrayList<Record> records = new ArrayList<>();

    // Widgets
    private ProgressBar progressBar;

    private static final String Mathias = "2016DERO01";
    private static final String Lucas = "2011ETTE01";

    private static final String WCA_ID = Lucas;
    private static final String BASE_URL = "https://www.worldcubeassociation.org/results/p.php?i=";



    /**
     * Le code non dépendant du layout lors de la creation du bundle
     * @param savedInstanceState bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // On initialise les composants

        activity = getActivity();
        preferences = activity.getSharedPreferences(Constants.SECRETS.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(activity);
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        // On initilalise l'adapter
        adapter = new RecordAdapter(records);

    }

    /**
     * Le code dépendant du layout lors de la creation du bundle
     * @param inflater inlfater
     * @param container vue parent
     * @param savedInstanceState bundle
     * @return vue enfant
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record, container, false);

        // On initialise le recyclerview
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.profile_recycler);
        assert recyclerView != null;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        recyclerView.setAdapter(adapter);

        // On initialise le progressbar
        progressBar = (ProgressBar) view.findViewById(R.id.profile_loader);

        progressBar.setVisibility(View.VISIBLE);

        // On charge les données si elles sont dans la RAM
        if(savedInstanceState != null && savedInstanceState.getString(Constants.STORAGE.RECORDS, null) != null) {


            String json = savedInstanceState.getString(Constants.STORAGE.RECORDS, null);

            if(json != null) {
                Type type = new TypeToken<ArrayList<Record>>() {}.getType();
                Gson gson = new Gson();

                ArrayList<Record> records = gson.fromJson(json, type);

                refreshData(records);

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


        return view;
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
        String json = gson.toJson(records);

        outState.putString(Constants.STORAGE.RECORDS, json);
    }

    @Override
    public String getTitle() {
        return activity != null ? activity.getString(R.string.title_activity_record) : null;
    }

    /**
     * Retourne l'URL utilisée pour faire des requete http
     * @return url
     */
    public static String getUrl() {
        return BASE_URL + WCA_ID;
    }



    /**
     * Actualise les records
     * @param records records
     */
    private void refreshData(ArrayList<Record> records) {
        this.records.clear();
        this.records.addAll(records);
        adapter.notifyDataSetChanged();
    }

    /**
     * Récupere les records via une requete HTTP
     */
    private void requestData() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, getUrl(), new Response.Listener<String>() {

            @Override
            public void onResponse(final String response) {

                // Comme le traitement est lourd, on met le code dans un nouveau Thread

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        // On parse le document et recupere les records que l'on met dans un adapter
                        Document document = Jsoup.parse(response);

                        // On remplace les anciennes données par des nouvelles
                        final ArrayList<Record> records = RecordProvider.getRecord(activity, document, true);

                        // todo ne pas oublier d'ajouter le record pour faire des comparaisons
                        //HistoryProvider.getHistory(activity, document);

                        // On affiche les données et on coupe le chargement sur l'UI Thread

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshData(records);
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        // On sauvegarde le record en JSON via Gson
                        saveData(records);

                    }
                }).start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, activity.getString(R.string.error_connection), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        // On limite le nombre de requetes
        request.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

    private boolean loadLocalData() {

        String json = preferences.getString(Constants.STORAGE.RECORDS, null);

        // Si on a déja sauvegardé une copie, on charge la copie
        if(json != null) {
            Gson gson = new Gson();
            ArrayList<Record> records;


            // Tokentype recupere le type de l'objet puisque ArrayList est générique
            Type type = new TypeToken<ArrayList<Record>>() {}.getType();

            // On recupere le record en deserialisant l'objet
            records = gson.fromJson(json, type);

            // On ajoute les resultats et on les affiche
            refreshData(records);

            return true;
        }

        return false;
    }

    /**
     * Sauvegarde les records
     * @param records records
     */
    private void saveData(ArrayList<Record> records) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(records);
        editor.putString(Constants.STORAGE.RECORDS, json);
        editor.apply();
    }


}
