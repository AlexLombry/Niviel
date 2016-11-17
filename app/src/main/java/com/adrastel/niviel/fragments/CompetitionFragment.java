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
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.models.readable.competition.Competition;
import com.adrastel.niviel.models.readable.competition.Title;
import com.adrastel.niviel.providers.html.CompetitionProvider;
import com.adrastel.niviel.views.RecyclerViewCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CompetitionFragment extends BaseFragment {

    private Unbinder unbinder;
    private CompetitionAdapter adapter;

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
        callData();
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

        // todo : changer en wcaurl
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addEncodedPathSegment("competitions")
                .build();


        recyclerView.callData(url, new RecyclerViewCompat.SuccessCallback() {
            @Override
            public void onSuccess(String response) {
                Document document = Jsoup.parse(response);

                final ArrayList<Competition> competitions = CompetitionProvider.getCompetition(document, CompetitionProvider.UPCOMING_COMPS);


                for(Competition competition : competitions) {
                    Log.d(String.valueOf(competition));
                }
                Log.d("Size", String.valueOf(competitions.size()));

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Title title = new Title("Upcomming", competitions);

                        ArrayList<Title> titles = new ArrayList<Title>();
                        titles.add(title);
                        adapter.refreshData(titles);
                    }
                });
            }
        });

    }
}
