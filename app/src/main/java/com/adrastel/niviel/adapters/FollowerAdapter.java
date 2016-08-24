package com.adrastel.niviel.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.database.DatabaseHelper;
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
        @BindView(R.id.more) ImageButton more;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

        if(followers.size() == 0) {

            TextView textView = new TextView(view.getContext());
            parent.addView(textView);


        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Follower follower = followers.get(position);

        holder.firstLine.setText(follower.name());
        holder.secondLine.setText(follower.wca_id());
        holder.place.setVisibility(View.INVISIBLE);

        loadMenu(holder, follower);

    }

    private void loadMenu(ViewHolder holder, final Follower follower) {

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                PopupMenu menu = new PopupMenu(view.getContext(), view);
                menu.inflate(R.menu.menu_pop_follower);

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.unfollow:
                                onUnfollow(view.getContext(), follower);
                        }

                        return false;
                    }
                });

                menu.show();

            }
        });

    }

    private void onUnfollow(Context context, Follower follower) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        helper.deleteFollower(follower.wca_id());
        followers.remove(follower);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }
}
