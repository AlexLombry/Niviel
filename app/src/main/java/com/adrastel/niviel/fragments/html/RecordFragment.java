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

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.RecordAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.models.BaseModel;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.providers.RecordProvider;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecordFragment extends HtmlFragment<Record, RecordAdapter> {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private Activity activity;
    private RecordAdapter adapter = new RecordAdapter(getDatas());
    private Unbinder unbinder;
    private ConnectivityManager connectivityManager;

    private String url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);


        String wca_id = null;

        // On recupere l'id wca

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
        // On modifie l'url en fonction de l'id wca
        setUrl(wca_id);

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
        unbinder = ButterKnife.bind(this, view);

        progressBar.setVisibility(View.VISIBLE);


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

        if (savedInstanceState != null) {
            ArrayList<Record> records = savedInstanceState.getParcelableArrayList(Constants.EXTRAS.RECORDS);
            refreshData(records);
        }
        // Si on est connecté, on fait une requete HTTP, sinon on lit les données locales
        else if (Assets.isConnected(connectivityManager)) {

            requestData();
        } else {
            loadLocalData();
        }

        closeLoaders();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(Constants.EXTRAS.RECORDS, getDatas());

        super.onSaveInstanceState(outState);
    }


    /**
     * Retourne l'url de requete
     * @return url
     */
    @Override
    protected String getUrl() {
        return url;
    }

    private void setUrl(String wca_id) {
        url = "https://www.worldcubeassociation.org/results/p.php?i=" + wca_id;
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

    @Override
    public int getPrimaryColor() {
        return R.color.blue;
    }

    @Override
    public int getPrimaryDarkColor() {
        return R.color.blueDark;
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
                Log.d("post request");
                closeLoaders();
            }
        });
    }

    /**
     * Recupère les données dans l'appareil si il s'agit du bon profil
     */
    private void loadLocalData() {
        loadLocalData(new loadLocalDataCallback() {
            @Override
            public Type getType() {
                return new TypeToken<ArrayList<Record>>() {
                }.getType();
            }
        });
    }

    /**
     * Ferme les loaders
     */
    private void closeLoaders() {
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
    }

}
