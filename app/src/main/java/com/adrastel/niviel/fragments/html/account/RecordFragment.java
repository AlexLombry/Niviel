package com.adrastel.niviel.fragments.html.account;

import android.app.Activity;
import android.content.SharedPreferences;
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
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.managers.HttpManager;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.providers.html.RecordProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class RecordFragment extends BaseFragment {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private Activity activity;
    private Unbinder unbinder;

    private HttpManager httpManager;

    private RecordAdapter adapter;

    private SharedPreferences preferences;

    private static final String WCA_ID = Constants.EXTRAS.WCA_ID;
    private static final String RECORD = Constants.EXTRAS.RECORDS;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


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

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new RecordAdapter();
        adapter.setActivity(getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    /**
     * Quand l'activité est créee
     * @param savedInstanceState bundle
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        httpManager = new HttpManager(getActivity(), swipeRefresh, progressBar);

        if (savedInstanceState != null) {
            adapter.refreshData(loadLocalData(savedInstanceState));
            httpManager.stopLoaders();
        }
        // Si on est connecté, on fait une requete HTTP, sinon on lit les données locales
        else if (isConnected()) {
            requestData();
        } else {
            adapter.refreshData(loadLocalData());
            httpManager.stopLoaders();
        }

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

        saveDatas(outState, adapter.getDatas());

        super.onSaveInstanceState(outState);
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

    @Override
    public int getFabVisibility() {
        return View.VISIBLE;
    }

    @Override
    public int getFabIcon() {
        return R.drawable.ic_followers;
    }

    @Override
    public void onFabClick(View view) {

    }

    private void requestData() {
        HttpUrl url = new HttpUrl.Builder()

                // https://www.worldcubeassociation.org/results/p.php?i=
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addPathSegments("results/p.php")
                .addEncodedQueryParameter("i", "2016DERO01")
                .build();
        httpManager.callData(url, new HttpManager.SuccessCallback() {
            @Override
            public void onSuccess(String response) {
                Document document = Jsoup.parse(response);
                final ArrayList<Record> records = RecordProvider.getRecord(getActivity(), document);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(records);
                    }
                });

                saveDatas(records);

            }
        });
    }

    private ArrayList<Record> loadLocalData() {

        String json = preferences.getString(RECORD, null);

        return loadFromJson(json);
    }

    private ArrayList<Record> loadLocalData(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            return savedInstanceState.getParcelableArrayList(RECORD);
        }

        return new ArrayList<>();
    }

    private void saveDatas(ArrayList<Record> records) {
        Gson gson = new Gson();

        String json = gson.toJson(records);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(RECORD, json);
        editor.apply();
    }

    private void saveDatas(Bundle savedInstanceState, ArrayList<Record> records) {
        savedInstanceState.putParcelableArrayList(RECORD, records);
    }
    private ArrayList<Record> loadFromJson(String json) {
        if(json != null) {
            Gson gson = new Gson();
            return gson.fromJson(json, new TypeToken<ArrayList<Record>>(){}.getType());
        }

        else {
            return new ArrayList<>();
        }
    }

}
