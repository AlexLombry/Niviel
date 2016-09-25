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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class RankingAdapter extends WebAdapter<RankingAdapter.ViewHolder, Ranking> implements PauseResumeInterface, RecyclerView.OnItemTouchListener {


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
    private OnItemClickListener onItemClickListener;
    private GestureDetector gestureDetector;

    public RankingAdapter(FragmentActivity activity, OnItemClickListener listener) {
        super(activity);

        this.onItemClickListener = listener;
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
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
    public void onBindViewHolder(RankingAdapter.ViewHolder holder, int position) {

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
        loadMenu(holder, ranking);
    }

    @Override
    public int getItemCount() {
        return getDatas().size();
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

                            case R.id.goto_profile:
                                gotoProfile(ranking);
                                break;


                            case R.id.follow:
                                if(isFollowing) {
                                    item.setTitle(view.getContext().getString(R.string.follow));
                                    onUnfollow(ranking);
                                    invalidateCircleView(holder.rank, false);
                                }

                                else {
                                    item.setTitle(view.getContext().getString(R.string.unfollow));
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
        });
    }

    private void onFollow(final ViewHolder holder, Ranking ranking) {

        Intent intent = new Intent(getActivity(), EditRecordService.class);
        intent.putExtra(EditRecordService.WCA_ID, ranking.getWca_id());
        intent.putExtra(EditRecordService.USERNAME, ranking.getPerson());
        getActivity().startService(intent);


/*        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        helper.insertFollower(ranking.getPerson(), ranking.getWca_id(), System.currentTimeMillis());

        String confirmation = String.format(context.getString(R.string.toast_follow_confirmation), ranking.getPerson());
        Toast.makeText(context, confirmation, Toast.LENGTH_LONG).show();*/

    }

    private void onUnfollow(Ranking ranking) {

        Intent intent = new Intent(getActivity(), EditRecordService.class);
        intent.putExtra(EditRecordService.ACTION, EditRecordService.DELETE_RECORD);
        intent.putExtra(EditRecordService.WCA_ID, ranking.getWca_id());
        intent.putExtra(EditRecordService.USERNAME, ranking.getPerson());

        getActivity().startService(intent);

    }

    private void invalidateCircleView(CircleView circleView, boolean isFollowing) {
        int color = isFollowing ? Assets.getColor(getActivity(), R.color.green_600) : Assets.getColor(getActivity(), R.color.colorAccent);
        circleView.setBackground(color);
    }

    private void gotoProfile(Ranking ranking) {
        ProfileFragment fragment = ProfileFragment.newInstance(ProfileFragment.RECORD_TAB, ranking.getWca_id(), ranking.getPerson());
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

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

        int position = recyclerView.getChildAdapterPosition(view);

        Ranking ranking = getDatas().get(position);

        if(view != null && onItemClickListener != null && gestureDetector.onTouchEvent(e)) {
            onItemClickListener.onClick(view, ranking);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

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

    public interface OnItemClickListener {
        void onClick(View view, Ranking ranking);
    }


}
