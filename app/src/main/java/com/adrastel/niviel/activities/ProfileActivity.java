package com.adrastel.niviel.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.adrastel.niviel.Http.ErrorListener;
import com.adrastel.niviel.R;
import com.adrastel.niviel.WCA.RecordProvider;
import com.adrastel.niviel.Models.Record;
import com.adrastel.niviel.adapters.ProfileAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    // Composants
    private Context context;
    private RequestQueue requestQueue;
    private SharedPreferences preferences;
    private ConnectivityManager connectivityManager;

    // Widgets
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    // Adapter
    ProfileAdapter adapter;

    // Lists
    ArrayList<Record> records = new ArrayList<>();

    private static final String Mathias = "2016DERO01";
    private static final String Lucas = "2011ETTE01";

    private static final String WCA_ID = Mathias;
    private static final String BASE_URL = "https://www.worldcubeassociation.org/results/p.php?i=";

    /**
     * Définitions des variables
     *
     * @param savedInstanceState bunlde
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Definition de la vue
        setContentView(R.layout.activity_profile);

        // Définition de la barre d'outil
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Définition du context
        context = getApplicationContext();

        // On initialise les parametres
        preferences = context.getSharedPreferences(Constants.SECRETS.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // On initialise les queues
        requestQueue = Volley.newRequestQueue(this);

        // On initalise les managers
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // Définition des widget
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.profile_list);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.profile_refresh);

        // Assertions des widgets
        assert fab != null;
        assert recyclerView != null;
        assert refreshLayout != null;

        // Mise en place du recyclerview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Mise en place de l'adapter
        adapter = new ProfileAdapter(records);
        recyclerView.setAdapter(adapter);

        // Mise en place des listeners

        /**
         * Si on swipe, on raffrechie les données
         */
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                loadData();
            }
        });

        String json = preferences.getString(Constants.PREFERENCES.RECORDS, null);

        // Si on a déja sauvegardé une copie, on charge la copie
        if(json != null) {
            Gson gson = new Gson();
            ArrayList<Record> records;


            // Tokentype recupere le type de l'objet puisque ArrayList est générique
            Type type = new TypeToken<ArrayList<Record>>(){}.getType();

            // On recupere le record en deserialisant l'objet
            records = gson.fromJson(json, type);

            // On ajoute les resultats et on les affiche
            this.records.clear();
            this.records.addAll(records);
            adapter.notifyDataSetChanged();
        }

        // On charge les données si on est connecté

        if(Assets.isConnected(connectivityManager)) {
            loadData();
        }



    }

    /**
     * Définition de la vue du menu
     * @param menu Menu
     * @return true pour afficher le menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_record, menu);
        return true;
    }

    /**
     *
     * Quand une option du menu est cliquée
     *
     * @param item l'id de l'option
     * @return true pour arreter le processing du menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_reload:
                loadData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Formate l'URL de requête
     * @return URL
     */
    private String getUrl() {
        return BASE_URL + WCA_ID;
    }

    /**
     * Lit les données des records personnels, le transforme en objet, met en place une recycler view et son adapter et gere la progress bar
     *
     * @see Record
     */
    private void loadData() {
        refreshLayout.setRefreshing(true);


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
                        records.clear();
                        records.addAll(RecordProvider.getRecord(context, document, true));

                        Gson gson = new Gson();

                        Log.d(gson.toJson(records));

                        // todo ne pas oublier d'ajouter le record pour faire des comparaisons
                        //HistoryProvider.getHistory(context, document);

                        // On affiche les données et on coupe le chargement sur l'UI Thread

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                refreshLayout.setRefreshing(false);
                            }
                        });


                        // On sauvegarde le record en JSON via Gson
                        saveData(records);

                    }
                }).start();

            }
        }, new ErrorListener(context, refreshLayout));

        requestQueue.add(request);
    }

    private void saveData(ArrayList<Record> records) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(records);
        editor.putString(Constants.PREFERENCES.RECORDS, json);
        editor.apply();
        Log.d("Sauvegarde");
    }
}
