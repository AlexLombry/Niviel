package com.adrastel.niviel.fragments;

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
import android.widget.Toast;

import com.adrastel.niviel.BuildConfig;
import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.FollowerAdapter;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.views.RecyclerViewCompat;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FollowerFragment extends BaseFragment {


    @BindView(R.id.recycler_view) RecyclerViewCompat recyclerView;
    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.empty_view) TextView emptyView;

    private Unbinder unbinder;

    @Override
    public int getStyle() {
        return R.style.AppTheme_Followers;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setEnabled(false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.initRecyclerViewCompat(swipeRefreshLayout, progressBar, emptyView);

        recyclerView.hideAll();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        long personal_id = preferences.getLong(getString(R.string.pref_personal_id), -1);

        DatabaseHelper db = DatabaseHelper.getInstance(getContext());
        ArrayList<Follower> followers = db.selectAllFollowers();
        Follower personalProfile = null;


        // Déplace le profil personel en haut de la liste
        for(Follower follower : followers) {
            if(follower._id() == personal_id) {
                personalProfile = follower;
            }
        }

        if(personalProfile != null) {
            followers.remove(personalProfile);
            Collections.sort(followers, new Follower.Comparator());
            followers.add(0, personalProfile);
        }

        else {
            Collections.sort(followers, new Follower.Comparator());
        }


        FollowerAdapter adapter = new FollowerAdapter(getActivity(), followers);
        recyclerView.setAdapter(adapter);
        recyclerView.showRecycler();

        if(followers.size() == 0) {
            emptyView.setText(R.string.no_followers);
            recyclerView.showEmpty();
        }

        Tracker tracker = activity.getDefaultTracker();
        tracker.setScreenName(getString(R.string.follower_fragment));
        tracker.send(new HitBuilders.ScreenViewBuilder()
                .setCustomMetric(1, followers.size(

                ))
                .build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        setTargetFragment(null, -1);
        super.onSaveInstanceState(outState);
    }
}
