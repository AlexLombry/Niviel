package com.adrastel.niviel.fragments.html;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.providers.HistoryProvider;
import com.adrastel.niviel.adapters.HistoryAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.models.BaseModel;
import com.adrastel.niviel.models.readable.History;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryFragment extends HtmlFragment<History, HistoryAdapter> {

    private Activity activity;
    private ConnectivityManager connectivityManager;
    private HistoryAdapter adapter = new HistoryAdapter(getDatas());
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private String url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);


        String wca_id = null;

        Bundle arguments = getArguments();

        if(arguments != null) {
            wca_id = arguments.getString(Constants.EXTRAS.WCA_ID, null);
        }

        // Si il est null on l'id wca est donc personel
        if(wca_id == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

            // On recupere l'id wca
            wca_id = preferences.getString(getString(R.string.pref_wca_id), null);

        }

        else {
            Toast.makeText(getContext(), R.string.wca_id_empty, Toast.LENGTH_LONG).show();
        }
        // On modifie l'url en fonction de l'id wca
        setUrl(wca_id);
        adapter.setWca_id(wca_id);

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

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setEnabled(false);

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

        if(savedInstanceState != null) {

            ArrayList<History> histories = savedInstanceState.getParcelableArrayList(Constants.EXTRAS.HISTORY);

            refreshData(histories);
        }

        else if(getArguments() != null){
            Bundle arguments = getArguments();

            ArrayList<History> histories = arguments.getParcelableArrayList(Constants.EXTRAS.COMPETITIONS);


            if(histories != null) {
                refreshData(histories);
            }

            else {
                requestData();
            }
        }

        else if(Assets.isConnected(connectivityManager)) {
            requestData();
        }

        closeLoaders();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(Constants.EXTRAS.HISTORY, getDatas());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected String getUrl() {
        return url;
    }

    private void setUrl(String wca_id) {
        url = "https://www.worldcubeassociation.org/results/p.php?i=" + wca_id;
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

    @Override
    public int getPrimaryColor() {
        return R.color.green;
    }

    @Override
    public int getPrimaryDarkColor() {
        return R.color.greenDark;
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
                closeLoaders();
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

        closeLoaders();

    }

    private void closeLoaders() {
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
    }

}
