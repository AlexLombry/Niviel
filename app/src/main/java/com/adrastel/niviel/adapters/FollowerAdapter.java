package com.adrastel.niviel.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.fragments.ProfileFragment;
import com.adrastel.niviel.views.CircleView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowerAdapter extends BaseAdapter<RecyclerView.ViewHolder> {

    public static final int CONTENT_TYPE = 0;
    public static final int AD_TYPE = 1;

    public static final int DIVIDER = 6;

    /**
     * Number of ads
     */
    private int ad_count = 0;

    private ArrayList<Follower> followers;


    public FollowerAdapter(FragmentActivity activity, ArrayList<Follower> followers) {
        super(activity);
        this.followers = followers;

        ad_count = (int) Math.ceil(this.followers.size() / DIVIDER);
    }

    public class FollowerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.place) CircleView place;
        @BindView(R.id.first_line) TextView firstLine;
        @BindView(R.id.second_line) TextView secondLine;
        @BindView(R.id.more) ImageButton more;
        @BindView(R.id.root_layout) LinearLayout click_area;

        public FollowerHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class AdHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.adview) AdView adView;
        public AdHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }



    @Override
    public int getItemViewType(int position) {
        if(position % DIVIDER == 0) {
            return AD_TYPE;
        }

        return CONTENT_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder   onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(viewType == AD_TYPE) {
            View view = inflater.inflate(R.layout.ad_view, parent, false);
            return new AdHolder(view);
        }

        else {
            View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

            return new FollowerHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, int position) {

        if(recyclerHolder instanceof AdHolder) {

            AdHolder holder = (AdHolder) recyclerHolder;


            try {
                MobileAds.initialize(getActivity(), "ca-app-pub-4938379788839148/5583565117");

                AdRequest adRequest = new AdRequest.Builder().addTestDevice("60C90B1288225E9B7FDB8AB3972CC7E5").build();
                holder.adView.loadAd(adRequest);
            }
            catch (Exception e) {
                e.printStackTrace();
            }


        } else if(recyclerHolder instanceof FollowerHolder) {
            FollowerHolder holder = (FollowerHolder) recyclerHolder;

            final Follower follower = followers.get(position);

            holder.firstLine.setText(follower.name());
            holder.secondLine.setText(follower.wca_id());
            holder.place.setBackground(Assets.getColor(getActivity(), R.color.green_300));
            holder.place.setText(String.valueOf(position + 1));

            holder.click_area.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileFragment profileFragment = ProfileFragment.newInstance(follower._id());

                    getActivity().switchFragment(profileFragment);
                }
            });

            if (Assets.isPersonal(getActivity(), follower._id())) {
                holder.click_area.setBackgroundResource(R.color.background_personal_follower);
                holder.more.setVisibility(View.INVISIBLE);
            } else {
                loadMenu(holder, follower);
            }
        }

    }

    private void loadMenu(FollowerHolder holder, final Follower follower) {

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
        helper.deleteFollower(follower._id());
        followers.remove(follower);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return followers.size() + ad_count;
    }
}
