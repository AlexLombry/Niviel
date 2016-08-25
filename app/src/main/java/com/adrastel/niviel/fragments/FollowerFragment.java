package com.adrastel.niviel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.FollowerAdapter;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FollowerFragment extends BaseFragment {


    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.progress) ProgressBar progressBar;

    private Unbinder unbinder;


    @Override
    public int getTitle() {
        return R.string.followers;
    }

    @Override
    public int getPrimaryColor() {
        return R.color.green;
    }

    @Override
    public int getPrimaryDarkColor() {
        return R.color.greenDark;
    }

    @Override
    public int getFabVisibility() {
        return View.GONE;
    }

    @Override
    public int getFabIcon() {
        return R.drawable.ic_followers;
    }

    @Override
    public void onFabClick(View view) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        progressBar.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DatabaseHelper db = DatabaseHelper.getInstance(getContext());
        ArrayList<Follower> followers = db.selectAllFollowers();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FollowerAdapter adapter = new FollowerAdapter(followers);
        recyclerView.setAdapter(adapter);

        if(followers.size() == 0) {
            // todo: intent to fragment
            makeSnackbar(R.string.no_followers, Snackbar.LENGTH_INDEFINITE).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
