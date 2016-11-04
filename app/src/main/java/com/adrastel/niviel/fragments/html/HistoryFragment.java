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
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.HistoryAdapter;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.managers.HttpManager;
import com.adrastel.niviel.models.readable.Event;
import com.adrastel.niviel.models.readable.History;
import com.adrastel.niviel.providers.html.HistoryProvider;
import com.adrastel.niviel.views.RecyclerViewCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class HistoryFragment extends BaseFragment {

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerViewCompat recyclerView;
    @BindView(R.id.empty_view) TextView emptyView;

    private Unbinder unbinder;
    private HistoryAdapter adapter;
    private HttpManager httpManager;

    private String wca_id = null;
    private long follower_id = -1;


    public static HistoryFragment newInstance(long follower_id) {
        HistoryFragment instance = new HistoryFragment();

        Bundle args = new Bundle();
        args.putLong(Constants.EXTRAS.ID, follower_id);

        instance.setArguments(args);
        return instance;
    }

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

        Bundle arguments = getArguments();

        if(arguments != null) {
            follower_id = arguments.getLong(Constants.EXTRAS.ID, -1);

            wca_id = follower_id == -1 ? arguments.getString(Constants.EXTRAS.WCA_ID, null) : null;
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

        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        recyclerView.setEmptyView(emptyView);
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

            Follower follower = database.selectFollowerFromId(follower_id);
            wca_id = follower.wca_id();

            ArrayList<com.adrastel.niviel.database.History> localHistories = database.selectHistoriesFromFollower(follower_id);
            ArrayList<History> histories = new ArrayList<>();

            for(com.adrastel.niviel.database.History history : localHistories) {
                histories.add(history.toHistoryModel());
            }

            adapter.refreshData(makeExpandedArrayList(histories));
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

        try {
            outState.putParcelableArrayList(Constants.EXTRAS.HISTORY, adapter.getDatas());

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    public void callData() {

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addEncodedPathSegments("results/p.php")
                .addEncodedQueryParameter("i", wca_id)
                .build();


        httpManager.callData(url, new HttpManager.SuccessCallback() {
            @Override
            public void onSuccess(String response) {
                Document document = Jsoup.parse(response);

                final ArrayList<History> histories = HistoryProvider.getHistory(activity, document);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(makeExpandedArrayList(histories));
                    }
                });
            }
        });

    }

    protected ArrayList<Event> loadLocalData(Bundle savedInstanceState) {
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
    public static ArrayList<Event> makeExpandedArrayList(ArrayList<History> histories) {

        Hashtable<String, ArrayList<History>> hashtable = new Hashtable<>();
        ArrayList<Event> events = new ArrayList<>();

        // Pour chaque historique, les trie dans un HashTable avec pour clé l'event
        for(History history : histories) {

            if(hashtable.containsKey(history.getEvent())) {

                ArrayList<History> oldHistory = hashtable.get(history.getEvent());
                oldHistory.add(history);

                hashtable.put(history.getEvent(), oldHistory);

            }

            else {

                ArrayList<History> oldHistories = new ArrayList<>();
                oldHistories.add(history);

                hashtable.put(history.getEvent(), oldHistories);
            }
        }


        // Convertie le HashTable en Event
        for(Map.Entry<String, ArrayList<History>> value : hashtable.entrySet()) {

            Event event = new Event(value.getKey(), value.getValue());
            events.add(event);
        }

        return events;

    }

}
