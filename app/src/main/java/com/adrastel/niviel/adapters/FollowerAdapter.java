package com.adrastel.niviel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.views.CircleView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowerAdapter extends BaseAdapter<FollowerAdapter.ViewHolder> {

    private ArrayList<Follower> followers;

    public FollowerAdapter(ArrayList<Follower> followers) {
        this.followers = followers;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_place) CircleView place;
        @BindView(R.id.first_line) TextView firstLine;
        @BindView(R.id.second_line) TextView secondLine;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Follower follower = followers.get(position);

        holder.firstLine.setText(follower.name());
        holder.secondLine.setText(follower.wca_id());
        holder.place.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return followers.size();
    }
}
