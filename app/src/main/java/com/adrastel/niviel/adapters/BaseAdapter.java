package com.adrastel.niviel.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    private FragmentActivity activity;

    public BaseAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    protected FragmentActivity getActivity() {
        return activity;
    }
}
