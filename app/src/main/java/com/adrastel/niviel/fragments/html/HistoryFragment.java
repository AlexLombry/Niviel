package com.adrastel.niviel.fragments.html;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.adrastel.niviel.activities.SettingsActivity;
import com.adrastel.niviel.adapters.HistoryAdapter;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.managers.HttpManager;
import com.adrastel.niviel.models.readable.History;
import com.adrastel.niviel.providers.html.HistoryProvider;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class HistoryFragment extends HtmlFragment<History> {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private Unbinder unbinder;
    private Activity activity;
    private HistoryAdapter adapter;
    private HttpManager httpManager;
    private HttpUrl.Builder urlBuilder = new HttpUrl.Builder();


    // todo: tout changer en new instance
    public static HistoryFragment newInstance(String wca_id) {
        HistoryFragment instance = new HistoryFragment();

        Bundle args = new Bundle();
        args.putString(Constants.EXTRAS.WCA_ID, wca_id);

        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

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

            if(wca_id == null) {
                makeSnackbar(R.string.wrong_wca_id, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), SettingsActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }

        }

        else {
            Toast.makeText(getContext(), R.string.wrong_wca_id, Toast.LENGTH_LONG).show();
        }
        // modifie l'url en fonction de l'id wca
        urlBuilder.addEncodedQueryParameter("i", wca_id);

        // cree l'adapter
        adapter = new HistoryAdapter(getActivity(), wca_id);

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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

        if(savedInstanceState != null) {
            adapter.refreshData(loadLocalData(savedInstanceState));
            httpManager.stopLoaders();
        }

        else if(getArguments() != null){
            Bundle arguments = getArguments();

            ArrayList<History> histories = arguments.getParcelableArrayList(Constants.EXTRAS.COMPETITIONS);


            if(histories != null) {
                adapter.refreshData(histories);
                httpManager.stopLoaders();
            }

            else if(isConnected()){
                callData();
            }

            else {
                adapter.refreshData(loadLocalData());
                httpManager.stopLoaders();
            }
        }

        else if(isConnected()) {
            callData();
        }

        else {
            adapter.refreshData(loadLocalData());
            httpManager.stopLoaders();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(Constants.EXTRAS.HISTORY, adapter.getDatas());

        super.onSaveInstanceState(outState);
    }

    @Override
    public String getStorageLocation() {
        return Constants.EXTRAS.HISTORY;
    }

    @Override
    public Type getStorageType() {
        return new TypeToken<ArrayList<History>>(){}.getType();
    }

    @Override
    public void callData() {

        HttpUrl url = urlBuilder
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addEncodedPathSegments("results/p.php")
                .build();


        httpManager.callData(url, new HttpManager.SuccessCallback() {
            @Override
            public void onSuccess(String response) {
                Document document = Jsoup.parse(response);

                final ArrayList<History> histories = HistoryProvider.getHistory(activity, document);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(histories);
                    }
                });

                saveDatas(histories);
            }
        });

    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_History;
    }

}
