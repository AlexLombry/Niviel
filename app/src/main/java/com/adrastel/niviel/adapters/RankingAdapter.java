package com.adrastel.niviel.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
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
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.dialogs.RankingDetailsDialog;
import com.adrastel.niviel.fragments.ProfileFragment;
import com.adrastel.niviel.models.readable.Ranking;
import com.adrastel.niviel.services.EditRecordService;
import com.adrastel.niviel.views.CircleView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingAdapter extends WebAdapter<RecyclerView.ViewHolder, Ranking> implements AdInterface {


    private int adCount = 0;
    private final static int CONTENT_TYPE = 0;

    private Handler handler = new Handler(Looper.getMainLooper());

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

    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(EditRecordService.INTENT_FILTER));
    }

    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());


        if(viewType == AD_TYPE) {
            View view = inflater.inflate(R.layout.ad_view, parent, false);
            return new AdHolder(view);
        }

        else {
            View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, int position) {

        if(recyclerHolder != null && recyclerHolder instanceof AdHolder) {

            final AdHolder holder = (AdHolder) recyclerHolder;

            try {


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MobileAds.initialize(getActivity(), "ca-app-pub-4938379788839148~5723165913");

                        AdRequest adRequest = new AdRequest.Builder()
                                .addTestDevice("60C90B1288225E9B7FDB8AB3972CC7E5")
                                .setBirthday(new GregorianCalendar(2000, 1, 1).getTime())
                                .setGender(AdRequest.GENDER_MALE)
                                .tagForChildDirectedTreatment(true)
                                .build();
                        holder.adView.loadAd(adRequest);
                    }
                }, 2000);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(recyclerHolder != null && recyclerHolder instanceof ViewHolder) {
            final ViewHolder holder = (ViewHolder) recyclerHolder;

            final Ranking ranking = getDatas().get((int) (position - Math.ceil(position / DIVIDER)));

            final String rank = ranking.getRank();
            final String person = ranking.getPerson();
            final String result = ranking.getResult();
            final String details = ranking.getDetails();

            holder.rank.setText(rank);
            holder.person.setText(person);

            if (isSingle) {
                holder.result.setText(result);
            } else {

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
    }

    @Override
    public void refreshData(ArrayList<Ranking> datas) {
        adCount = (int) Math.ceil(datas.size() / DIVIDER);
        super.refreshData(datas);
    }

    @Override
    public int getItemCount() {
        return getDatas().size() + adCount;
    }

    @Override
    public int getItemViewType(int position) {
        if(position % DIVIDER == 0) {
            return AD_TYPE;
        }

        else {
            return CONTENT_TYPE;
        }
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    private void loadMenu(View view, final RankingAdapter.ViewHolder holder, final Ranking ranking) {
        final boolean isFollowing = Assets.isFollowing(getActivity(), ranking.getWca_id());

        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.menu_pop_ranking);


        String followTitle = isFollowing ? getString(R.string.unfollow) : getString(R.string.follow);

        MenuItem followButton = popupMenu.getMenu().findItem(R.id.follow);
        followButton.setTitle(followTitle);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.details:
                        onDetails(ranking);
                        return true;

                    case R.id.share:
                        onShare(ranking);
                        return true;

                    case R.id.goto_profile:
                        gotoProfile(ranking);
                        break;


                    case R.id.follow:
                        if(isFollowing) {
                            item.setTitle(getString(R.string.follow));
                            onUnfollow(ranking);
                            invalidateCircleView(holder.rank, false);
                        }

                        else {
                            item.setTitle(getString(R.string.unfollow));
                            onFollow(ranking);
                        }
                        return true;

                }

                return false;
            }
        });
        popupMenu.show();
    }

    private void onFollow(Ranking ranking) {

        Intent intent = new Intent(getActivity(), EditRecordService.class);
        intent.putExtra(EditRecordService.WCA_ID, ranking.getWca_id());
        intent.putExtra(EditRecordService.USERNAME, ranking.getPerson());
        getActivity().startService(intent);

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
        ProfileFragment fragment = ProfileFragment.newInstance(ranking.getWca_id(), ranking.getPerson());
        getActivity().switchFragment(fragment);

    }

    private void onDetails(Ranking ranking) {

        DialogFragment rankingDialog = RankingDetailsDialog.newInstance(ranking);
        rankingDialog.show(getActivity().getSupportFragmentManager(), "details");

    }

    private void onShare(Ranking ranking) {

        String url = new WcaUrl()
                .profile(ranking.getWca_id())
                .toString();

        Intent intent = new Intent();

        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);

        Intent chooser = Intent.createChooser(intent, getString(R.string.share));

        getActivity().startActivity(chooser);
    }

    // Le view holder qui contient toutes les infos
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.place) CircleView rank;
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
