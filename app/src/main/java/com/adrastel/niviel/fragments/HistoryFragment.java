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

import com.adrastel.niviel.Models.BaseModel;
import com.adrastel.niviel.Models.History;
import com.adrastel.niviel.R;
import com.adrastel.niviel.WCA.HistoryProvider;
import com.adrastel.niviel.adapters.HistoryAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryFragment extends GenericFragment<History, HistoryAdapter> {

    Activity activity;
    Context context;
    ConnectivityManager connectivityManager;
    HistoryAdapter adapter = new HistoryAdapter(getDatas());
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
    protected String getUrl() {
        return RecordFragment.getStaticUrl();
    }

    @Override
    protected HistoryAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected String getStorage() {
        return Constants.STORAGE.HISTORY;
    }

    @Override
    public int getTitle() {
        return R.string.title_activity_history;
    }

    /**
     * Fait une requete HTTP
     */
    private void requestData() {

        super.requestData(activity, new requestDataCallback() {
            @Override
            public ArrayList<? extends BaseModel> parseDatas(Document document) {
                return HistoryProvider.getHistory(activity, document);
            }

            @Override
            public void onSuccess(ArrayList<? extends BaseModel> datas) {

                // On sauvegarde et raffrechie la liste
                refreshAndSaveData((ArrayList<History>) datas);
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
                return new TypeToken<ArrayList<History>>() {}.getType();
            }
        });

        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
    }
}
