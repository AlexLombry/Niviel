package com.adrastel.niviel.fragments.html;

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
import com.adrastel.niviel.adapters.HistoryAdapter;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.managers.HttpManager;
import com.adrastel.niviel.models.readable.History;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.providers.html.HistoryProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class HistoryFragment extends BaseFragment {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private Unbinder unbinder;
    private HistoryAdapter adapter;
    private HttpManager httpManager;
    private HttpUrl.Builder urlBuilder = new HttpUrl.Builder();

    private String wca_id = null;
    private long follower_id = -1;


    public static HistoryFragment newInstance(long follower_id) {
        HistoryFragment instance = new HistoryFragment();

        Bundle args = new Bundle();
        args.putLong(Constants.EXTRAS.ID, follower_id);

        instance.setArguments(args);
        return instance;
    }
    // todo: tout changer en new instance
    public static HistoryFragment newInstance(String wca_id, String username) {
        HistoryFragment instance = new HistoryFragment();

        Bundle args = new Bundle();
        args.putString(Constants.EXTRAS.WCA_ID, wca_id);
        args.putString(Constants.EXTRAS.USERNAME, username);

        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String username = null;

        Bundle arguments = getArguments();

        if(arguments != null) {
            follower_id = arguments.getLong(Constants.EXTRAS.ID, -1);

            wca_id = follower_id == -1 ? arguments.getString(Constants.EXTRAS.WCA_ID, null) : null;
        }

        // modifie l'url en fonction de l'id wca
        urlBuilder.addEncodedQueryParameter("i", wca_id);

        // cree l'adapter
        adapter = new HistoryAdapter(getActivity());

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

        else if(follower_id != -1) {

            DatabaseHelper database = DatabaseHelper.getInstance(getContext());

            ArrayList<com.adrastel.niviel.database.History> localHistories = database.selectHistoriesFromFollower(follower_id);
            ArrayList<History> histories = new ArrayList<>();

            for(com.adrastel.niviel.database.History history : localHistories) {
                histories.add(history.toHistoryModel());
            }

            adapter.refreshData(histories);
            httpManager.stopLoaders();
        }

        else if(isConnected()) {
            callData();
        }

        else {
            httpManager.stopLoaders();
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

        outState.putParcelableArrayList(Constants.EXTRAS.HISTORY, adapter.getDatas());

        super.onSaveInstanceState(outState);
    }

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
            }
        });

    }

    protected ArrayList<History> loadLocalData(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            return savedInstanceState.getParcelableArrayList(Constants.EXTRAS.HISTORY);
        }

        return new ArrayList<>();
    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_History;
    }

    /**
     * Transforme un arraylist d'histoires en arraylist d'arraylist
     * @param histories historique
     * @return arraylist d'arraylist
     */
    public HashMap<String, ArrayList<History>> makeExpandedArrayList(ArrayList<History> histories) {

        // Retour
        HashMap<String, ArrayList<History>> events = new HashMap<>();

        // Variable temporaire
        String tokenEvent = histories.size() > 0 ? histories.get(0).getEvent() : null;
        ArrayList<History> tokenHistories = new ArrayList<>();

        /*
        Pour chaque historique:
            - Ajoute l'historique en cours dans une variable temporaire
            - Si l'event actuel est different du précédent, on ajoute les historiques precedent dans une variable
         */
        for(History history : histories) {

            if(tokenEvent != null && !tokenEvent.equals(history.getEvent())) {

                events.put(tokenEvent, histories);

                tokenEvent = history.getEvent();
                tokenHistories.clear();
                tokenHistories = new ArrayList<>();
            }

            tokenHistories.add(history);

        }

        return events;

    }

}
