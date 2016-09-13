package com.adrastel.niviel.fragments.html;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.RecordAdapter;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.managers.HttpManager;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.providers.html.RecordProvider;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class RecordFragment extends HtmlFragment<Record> {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private Unbinder unbinder;

    private HttpManager httpManager;

    private RecordAdapter adapter;

    private String wca_id;

    // todo: utiliser les get instance
    public static RecordFragment newInstance(String wca_id) {

        RecordFragment instance = new RecordFragment();

        Bundle args = new Bundle();
        args.putString(Constants.EXTRAS.WCA_ID ,wca_id);

        instance.setArguments(args);

        return instance;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new RecordAdapter(getActivity());

        wca_id = null;

        // On recupere l'id wca

        Bundle arguments = getArguments();

        if(arguments != null) {
            wca_id = arguments.getString(Constants.EXTRAS.WCA_ID, null);
        }

        // Si il est null on l'id wca est donc personel
        if(wca_id == null) {

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
            callData();
        } else if(Assets.isPersonal(getContext(), wca_id)){
            adapter.refreshData(loadLocalData());
            httpManager.stopLoaders();
        }

        else {
            Toast.makeText(getContext(), R.string.error_connection, Toast.LENGTH_LONG).show();
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callData();
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
        super.onSaveInstanceState(outState);
        try {
            outState.putParcelableArrayList(Constants.EXTRAS.RECORDS, adapter.getDatas());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getStorageLocation() {
        return Constants.EXTRAS.RECORDS;
    }

    @Override
    public Type getStorageType() {
        return new TypeToken<ArrayList<Record>>(){}.getType();
    }


    @Override
    public int getStyle() {
        return R.style.AppTheme_Records;
    }

    @Override
    public void callData() {
        HttpUrl url = new HttpUrl.Builder()

                // https://www.worldcubeassociation.org/results/p.php?i=
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addPathSegments("results/p.php")
                .addEncodedQueryParameter("i", wca_id)
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

                if(Assets.isPersonal(getContext(), wca_id)) {
                    saveDatas(records);
                }

            }
        });
    }

}
