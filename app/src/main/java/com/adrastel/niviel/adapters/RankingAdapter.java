package com.adrastel.niviel.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.IntentHelper;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.dialogs.RankingDetailsDialog;
import com.adrastel.niviel.fragments.html.account.HistoryFragment;
import com.adrastel.niviel.fragments.html.account.RecordFragment;
import com.adrastel.niviel.models.readable.Ranking;
import com.adrastel.niviel.views.CircleView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingAdapter extends BaseAdapter<RankingAdapter.ViewHolder> {

    private ArrayList<Ranking> rankings;
    private boolean isSingle = true;

    public RankingAdapter(ArrayList<Ranking> rankings){
        this.rankings = rankings;
    }



    // Le view holder qui contient toutes les infos
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_place) CircleView rank;
        @BindView(R.id.first_line) TextView person;
        @BindView(R.id.second_line) TextView result;
        @BindView(R.id.more) ImageButton more;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankingAdapter.ViewHolder holder, int position) {

        final Ranking ranking = rankings.get(position);

        final String rank = ranking.getRank();
        final String person = ranking.getPerson();
        final String result = ranking.getResult();
        final String details = ranking.getDetails();

        holder.rank.setText(rank);
        holder.person.setText(person);

        if(isSingle) {
            holder.result.setText(result);
        }

        else {

            holder.result.setText(Assets.formatHtmlAverageDetails(result, details));

        }

        final boolean isFollowing = Assets.isFollowing(getActivity(), ranking.getWca_id());

        // Actualise le circle view
        invalidateCircleView(holder.rank, isFollowing);

        // charge le menu
        loadMenu(holder, ranking);
    }

    @Override
    public int getItemCount() {
        return rankings.size();
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    private void loadMenu(final RankingAdapter.ViewHolder holder, final Ranking ranking) {
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final boolean isFollowing = Assets.isFollowing(getActivity(), ranking.getWca_id());

                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.menu_pop_ranking);


                String followTitle = isFollowing ? view.getContext().getString(R.string.unfollow) : view.getContext().getString(R.string.follow);

                MenuItem followButton = popupMenu.getMenu().findItem(R.id.follow);
                followButton.setTitle(followTitle);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.details:
                                onDetails(getActivity().getSupportFragmentManager(), ranking);
                                return true;

                            case R.id.share:
                                onShare(view.getContext(), ranking);
                                return true;

                            case R.id.goto_records:
                                gotoRecords(ranking);
                                return true;

                            case R.id.goto_history:
                                gotoHistory(ranking);
                                return true;

                            case R.id.follow:
                                if(isFollowing) {
                                    item.setTitle(view.getContext().getString(R.string.follow));
                                    onUnfollow(view.getContext(), ranking);
                                    invalidateCircleView(holder.rank, false);
                                }

                                else {
                                    item.setTitle(view.getContext().getString(R.string.unfollow));
                                    onFollow(view.getContext(), ranking);
                                    invalidateCircleView(holder.rank, true);
                                }
                                return true;

                        }

                        return false;
                    }
                });
                popupMenu.show();

            }
        });
    }

    private void onFollow(Context context, Ranking ranking) {

        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        helper.insertFollower(ranking.getPerson(), ranking.getWca_id(), System.currentTimeMillis());

        String confirmation = String.format(context.getString(R.string.toast_follow_confirmation), ranking.getPerson());
        Toast.makeText(context, confirmation, Toast.LENGTH_LONG).show();

    }

    private void onUnfollow(Context context, Ranking ranking) {

        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        helper.deleteFollower(ranking.getWca_id());

        ArrayList<Follower> followers = helper.selectAllFollowers();
        Log.d(ranking.getWca_id());
        for(Follower follower : followers) {
            Log.d(follower.wca_id());
        }

        String confirmation = String.format(context.getString(R.string.toast_unfollow_confirmation), ranking.getPerson());
        Toast.makeText(context, confirmation, Toast.LENGTH_LONG).show();
    }

    private void invalidateCircleView(CircleView circleView, boolean isFollowing) {
        int color = isFollowing ? Assets.getColor(getActivity(), R.color.green) : Assets.getColor(getActivity(), R.color.colorAccent);
        circleView.setBackground(color);
    }

    private void gotoRecords(Ranking ranking) {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRAS.WCA_ID, ranking.getWca_id());

        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(bundle);

        IntentHelper.switchFragment(getActivity(), fragment);

    }

    private void gotoHistory(Ranking ranking) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRAS.WCA_ID, ranking.getWca_id());

        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(bundle);

        IntentHelper.switchFragment(getActivity(), fragment);
    }

    private void onDetails(FragmentManager fragmentManager, Ranking ranking) {

        if(fragmentManager != null) {

            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.EXTRAS.RANKING, ranking);

            DialogFragment rankingDialog = new RankingDetailsDialog();

            rankingDialog.setArguments(bundle);

            rankingDialog.show(fragmentManager, "details");


        }

    }

    private void onShare(Context context, Ranking ranking) {
        String shareFormat = context.getString(R.string.share_ranking);

        String text = String.format(shareFormat, ranking.getPerson(), ranking.getRank(), ranking.getResult());

        String html = String.format(shareFormat, Assets.wrapStrong(ranking.getPerson()),
                Assets.wrapStrong(ranking.getRank()), Assets.wrapStrong(ranking.getResult()));


        IntentHelper.shareIntent(context, text, html);
    }


}
