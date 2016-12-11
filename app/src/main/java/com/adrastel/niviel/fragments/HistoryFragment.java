package com.adrastel.niviel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.BaseActivity;
import com.adrastel.niviel.adapters.HistoryAdapter;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.models.readable.history.Event;
import com.adrastel.niviel.models.readable.history.History;
import com.adrastel.niviel.providers.html.HistoryProvider;
import com.adrastel.niviel.views.RecyclerViewCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class HistoryFragment extends BaseFragment {

    public static final String EVENTS = "events";
    public static final String SORT = "sort";

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerViewCompat recyclerView;
    @BindView(R.id.empty_view) TextView emptyView;

    private Unbinder unbinder;
    private HistoryAdapter adapter;

    private String wca_id = null;
    private long follower_id = -1;

    /**
     * 2 constantes qui déterminent si on veux trier par event ou competition
     */

    private boolean sortByEvent = true;


    public static HistoryFragment newInstance(long follower_id, boolean sort) {
        HistoryFragment instance = new HistoryFragment();

        Bundle args = new Bundle();
        args.putLong(BaseActivity.ID, follower_id);
        args.putBoolean(SORT, sort);

        instance.setArguments(args);
        return instance;
    }

    public static HistoryFragment newInstance(String wca_id, boolean sort) {
        HistoryFragment instance = new HistoryFragment();

        Bundle args = new Bundle();
        args.putString(BaseActivity.WCA_ID, wca_id);
        args.putBoolean(SORT, sort);

        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        if(arguments != null) {
            sortByEvent = arguments.getBoolean(SORT);

            follower_id = arguments.getLong(BaseActivity.ID, -1);
            wca_id = follower_id == -1 ? arguments.getString(BaseActivity.WCA_ID, null) : null;
        }


        adapter = new HistoryAdapter(getActivity(), new ArrayList<Event>());


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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        recyclerView.initRecyclerViewCompat(swipeRefresh, progressBar, emptyView);

        recyclerView.showProgress();

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
            ArrayList<Event> histories = savedInstanceState.getParcelableArrayList(EVENTS);
            adapter.refreshData(histories);
            recyclerView.showRecycler();

            // Hydrate la variable WCA_ID
            if(follower_id != -1) {
                DatabaseHelper database = DatabaseHelper.getInstance(getContext());

                Follower follower = database.selectFollowerFromId(follower_id);
                wca_id = follower.wca_id();
            }
        }

        else if(follower_id != -1) {

            DatabaseHelper database = DatabaseHelper.getInstance(getContext());

            Follower follower = database.selectFollowerFromId(follower_id);
            wca_id = follower.wca_id();

            ArrayList<com.adrastel.niviel.database.History> localHistories = database.selectHistoriesFromFollower(follower_id);
            ArrayList<History> histories = new ArrayList<>();

            for(com.adrastel.niviel.database.History history : localHistories) {
                histories.add(history.toHistoryModel());
            }

            adapter.refreshData(sortByEvent ? makeExpandedArrayList(histories, true) : makeExpandedArrayList(histories, false));
            recyclerView.showRecycler();
        }

        else if(isConnected()) {
            callData();
        }

        else {
            recyclerView.showEmpty();
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

        outState.putParcelableArrayList(EVENTS, adapter.getDatas());
        setTargetFragment(null, -1);

        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    public void callData() {

        HttpUrl url = new WcaUrl()
                .profile(wca_id)
                .build();

        recyclerView.callData(url, new RecyclerViewCompat.SuccessCallback() {
            @Override
            public void onSuccess(String response) {
                Document document = Jsoup.parse(response);

                final ArrayList<History> histories = HistoryProvider.getHistory(activity, document);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(sortByEvent ? makeExpandedArrayList(histories, true) : makeExpandedArrayList(histories, false));
                    }
                });
            }
        });

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
    public static ArrayList<Event> makeExpandedArrayList(ArrayList<History> histories, boolean sortByEvent) {

        Hashtable<String, ArrayList<History>> hashtable = new Hashtable<>();
        ArrayList<Event> events = new ArrayList<>();

        // Pour chaque historique, les trie dans un HashTable avec pour clé l'event
        for(History history : histories) {

            if(hashtable.containsKey(sortByEvent ? history.getEvent() : history.getCompetition())) {

                ArrayList<History> oldHistory = hashtable.get(sortByEvent ? history.getEvent() : history.getCompetition());
                oldHistory.add(history);

                hashtable.put(sortByEvent ? history.getEvent() : history.getCompetition(), oldHistory);

            }

            else {

                ArrayList<History> oldHistories = new ArrayList<>();
                oldHistories.add(history);

                hashtable.put(sortByEvent ? history.getEvent() : history.getCompetition(), oldHistories);
            }
        }


        // Convertie le HashTable en Event
        for(Map.Entry<String, ArrayList<History>> value : hashtable.entrySet()) {

            Event event = new Event(value.getKey(), sortByEvent, value.getValue());
            events.add(event);
        }

        return events;
    }

}
