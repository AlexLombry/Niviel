package com.adrastel.niviel.fragments;

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

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.FollowerAdapter;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FollowerFragment extends BaseFragment {


    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

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

        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setEnabled(false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DatabaseHelper db = DatabaseHelper.getInstance(getContext());
        ArrayList<Follower> followers = db.selectAllFollowers();



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FollowerAdapter adapter = new FollowerAdapter(getActivity(), followers);
        recyclerView.setAdapter(adapter);

        if(followers.size() == 0) {
            makeSnackbar(R.string.no_followers, Snackbar.LENGTH_INDEFINITE).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
