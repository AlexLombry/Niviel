package com.adrastel.niviel.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
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
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.IntentHelper;
import com.adrastel.niviel.dialogs.RankingDetailsDialog;
import com.adrastel.niviel.fragments.ProfileFragment;
import com.adrastel.niviel.interfaces.PauseResumeInterface;
import com.adrastel.niviel.models.readable.Ranking;
import com.adrastel.niviel.services.EditRecordService;
import com.adrastel.niviel.views.CircleView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingAdapter extends WebAdapter<RankingAdapter.ViewHolder, Ranking> implements PauseResumeInterface {


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getIntExtra(EditRecordService.ACTION, EditRecordService.ADD_RECORD_FAILURE)) {

                case EditRecordService.ADD_RECORD_SUCCESS:
                    notifyDataSetChanged();
                    break;

                case EditRecordService.ADD_RECORD_FAILURE:
                    notifyDataSetChanged();
                    break;
            }

        }
    };

    private boolean isSingle = true;

    public RankingAdapter(FragmentActivity activity) {
        super(activity);

    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(EditRecordService.INTENT_FILTER));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RankingAdapter.ViewHolder holder, int position) {

        final Ranking ranking = getDatas().get(position);

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
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMenu(view, holder, ranking);
            }
        });

        holder.click_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoProfile(ranking);
            }
        });

        holder.click_area.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                loadMenu(view, holder, ranking);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return getDatas().size();
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    private void loadMenu(View view, final RankingAdapter.ViewHolder holder, final Ranking ranking) {
        final boolean isFollowing = Assets.isFollowing(getActivity(), ranking.getWca_id());

        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.menu_pop_ranking);


        String followTitle = isFollowing ? getActivity().getString(R.string.unfollow) : getActivity().getString(R.string.follow);

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
                        onShare(getActivity(), ranking);
                        return true;

                    case R.id.goto_profile:
                        gotoProfile(ranking);
                        break;


                    case R.id.follow:
                        if(isFollowing) {
                            item.setTitle(getActivity().getString(R.string.follow));
                            onUnfollow(ranking);
                            invalidateCircleView(holder.rank, false);
                        }

                        else {
                            item.setTitle(getActivity().getString(R.string.unfollow));
                            onFollow(holder, ranking);
                            //invalidateCircleView(holder.rank, true);
                        }
                        return true;

                }

                return false;
            }
        });
        popupMenu.show();
    }

    private void onFollow(final ViewHolder holder, Ranking ranking) {

        Intent intent = new Intent(getActivity(), EditRecordService.class);
        intent.putExtra(EditRecordService.WCA_ID, ranking.getWca_id());
        intent.putExtra(EditRecordService.USERNAME, ranking.getPerson());
        getActivity().startService(intent);


/*        DatabaseHelper helper = DatabaseHelper.newInstance(context);
        helper.insertFollower(ranking.getPerson(), ranking.getWca_id(), System.currentTimeMillis());

        String confirmation = String.format(context.getString(R.string.toast_follow_confirmation), ranking.getPerson());
        Toast.makeText(context, confirmation, Toast.LENGTH_LONG).show();*/

    }

    private void onUnfollow(Ranking ranking) {

        Intent intent = new Intent(getActivity(), EditRecordService.class);
        intent.putExtra(EditRecordService.ACTION, EditRecordService.DELETE_FOLLOWER);
        intent.putExtra(EditRecordService.WCA_ID, ranking.getWca_id());
        intent.putExtra(EditRecordService.USERNAME, ranking.getPerson());

        getActivity().startService(intent);

    }

    private void invalidateCircleView(CircleView circleView, boolean isFollowing) {
        int color = isFollowing ? Assets.getColor(getActivity(), R.color.green_300) : Assets.getColor(getActivity(), R.color.teal_300);
        circleView.setBackground(color);
    }

    private void gotoProfile(Ranking ranking) {
        ProfileFragment fragment = ProfileFragment.newInstance(ProfileFragment.RECORD_TAB, ranking.getWca_id(), ranking.getPerson());
        getActivity().switchFragment(fragment);

    }

    private void onDetails(FragmentManager fragmentManager, Ranking ranking) {

        if(fragmentManager != null) {

            DialogFragment rankingDialog = RankingDetailsDialog.newInstance(ranking);
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

    // Le view holder qui contient toutes les infos
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_place) CircleView rank;
        @BindView(R.id.first_line) TextView person;
        @BindView(R.id.second_line) TextView result;
        @BindView(R.id.more) ImageButton more;
        @BindView(R.id.root_layout) LinearLayout click_area;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
