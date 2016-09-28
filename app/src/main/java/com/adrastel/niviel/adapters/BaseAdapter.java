package com.adrastel.niviel.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

import com.adrastel.niviel.activities.MainActivity;

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    private MainActivity activity;

    public BaseAdapter(FragmentActivity activity) {
        try {
            this.activity = (MainActivity) activity;
        }
        catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    protected MainActivity getActivity() {
        return activity;
    }
}
