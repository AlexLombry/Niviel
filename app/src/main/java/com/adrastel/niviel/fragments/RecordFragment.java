package com.adrastel.niviel.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.adrastel.niviel.R;
import com.adrastel.niviel.WCA.RecordProvider;
import com.adrastel.niviel.adapters.RecordAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.models.BaseModel;
import com.adrastel.niviel.models.Record;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RecordFragment extends GenericFragment<Record, RecordAdapter> {

    Activity activity;
    RecordAdapter adapter = new RecordAdapter(getDatas());
    ConnectivityManager connectivityManager;
    SwipeRefreshLayout swipeRefresh;

    ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        adapter.setManager(getFragmentManager());

    }

    /**
     * Lors de la creation de la vue
     * @param inflater inflater
     * @param container parent
     * @param savedInstanceState bindle
     * @return vue
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.fragment_list_loader);
        progressBar.setVisibility(View.VISIBLE);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.fragment_list_swipe_refresh);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_list_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(getAdapter());

        return view;
    }

    /**
     * Quand l'activité est créee
     * @param savedInstanceState bundle
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Si on est connecté, on fait une requete HTTP, sinon on lit les données locales
        if(Assets.isConnected(connectivityManager)) {

            requestData();
        }

        else {

            loadLocalData();

        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });

    }

    /**
     * Retourne l'url de requete
     * @return url
     */
    @Override
    protected String getUrl() {
        return "https://www.worldcubeassociation.org/results/p.php?i=2016DERO01";
    }

    public static String getStaticUrl() {
        return "https://www.worldcubeassociation.org/results/p.php?i=2016DERO01";
    }

    /**
     * Retourne l'adapter utilisé
     * @return adapter
     */
    @Override
    protected RecordAdapter getAdapter() {
        return adapter;
    }

    /**
     * Retourne l'emplacement de stockage utilisé
     * @return stockage
     */
    @Override
    protected String getStorage() {
        return Constants.STORAGE.RECORDS;
    }

    /**
     * Retoune le titre du fragment
     * @return titre
     */
    @Override
    public int getTitle() {
        return R.string.personal_records;
    }

    /**
     * Fait une requete HTTP
     */
    private void requestData() {

        super.requestData(activity, new requestDataCallback() {
            @Override
            public ArrayList<? extends BaseModel> parseDatas(Document document) {
                return RecordProvider.getRecord(activity, document, true);
            }

            @Override
            public void onSuccess(ArrayList<? extends BaseModel> datas) {

                // On sauvegarde et raffrechie la liste
                refreshAndSaveData((ArrayList<Record>) datas);
            }

            @Override
            public void onError(VolleyError error) {
                loadLocalData();
            }

            @Override
            public void postRequest() {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    /**
     * Recupère les données dans l'appareil
     */
    private void loadLocalData() {
        loadLocalData(new loadLocalDataCallback() {
            @Override
            public Type getType() {
                return new TypeToken<ArrayList<Record>>() {}.getType();
            }
        });
    }

}
