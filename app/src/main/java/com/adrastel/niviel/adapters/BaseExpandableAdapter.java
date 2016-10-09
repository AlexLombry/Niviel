package com.adrastel.niviel.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.models.readable.Event;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseExpandableAdapter<PVH extends ParentViewHolder, CVH extends ChildViewHolder> extends ExpandableRecyclerAdapter<PVH, CVH> {

    private MainActivity activity;

    public BaseExpandableAdapter(FragmentActivity activity) {
        super(null);

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

    public ArrayList<Event> getDatas() {
        return (ArrayList<Event>) getParentItemList();
    }

    public void refreshData(ArrayList<Event> datas) {

        getDatas().clear();
        getDatas().addAll(datas);
        notifyDataSetChanged();
    }
}