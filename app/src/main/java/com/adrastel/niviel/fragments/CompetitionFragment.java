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
import com.adrastel.niviel.adapters.CompetitionAdapter;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.models.readable.competition.Competition;
import com.adrastel.niviel.models.readable.competition.Title;
import com.adrastel.niviel.providers.html.CompetitionProvider;
import com.adrastel.niviel.views.RecyclerViewCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class CompetitionFragment extends BaseFragment {

    private Unbinder unbinder;
    private CompetitionAdapter adapter;

    public static final String TITLES = "titles";

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerViewCompat recyclerView;
    @BindView(R.id.empty_view) TextView emptyView;

    public static CompetitionFragment newInstance() {
        return new CompetitionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new CompetitionAdapter(getActivity(), new ArrayList<Title>());
    }

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            ArrayList<Title> titles = savedInstanceState.getParcelableArrayList(TITLES);
            adapter.refreshData(titles);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TITLES, adapter.getDatas());
        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public int getStyle() {
        // todo : changer ça
        return R.style.AppTheme_Followers;
    }

    public void callData() {

        HttpUrl url = new WcaUrl()
                .competition()
                .build();


        recyclerView.callData(url, new RecyclerViewCompat.SuccessCallback() {
            @Override
            public void onSuccess(String response) {

                Document document = Jsoup.parse(response);

                final ArrayList<Title> titles = new ArrayList<>();

                // In progress
                Title inProgress = treatData(document, CompetitionProvider.IN_PROGRESS, getString(R.string.in_progress_competitions));

                // Upcomming
                Title upcomming = treatData(document, CompetitionProvider.UPCOMING_COMPS, getString(R.string.upcomming_competitions));

                if(inProgress != null) {
                    titles.add(inProgress);
                }

                if(upcomming != null) {
                    titles.add(upcomming);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(titles);
                    }
                });

            }
        });
    }

    private Title treatData(Document document, String tag, String title) {
        final ArrayList<Competition> competitions = CompetitionProvider.getCompetition(document, tag);

        if(competitions.size() != 0 ){
            return new Title(title, competitions);
        }

        return null;
    }
}
