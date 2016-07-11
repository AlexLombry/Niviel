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

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.dialogs.RankingDetailsDialog;
import com.adrastel.niviel.models.Ranking;
import com.adrastel.niviel.views.CircleView;

import java.util.ArrayList;

public class RankingAdapter extends BaseAdapter<RankingAdapter.ViewHolder> {

    private ArrayList<Ranking> rankings;
    private boolean isSingle = true;
    private FragmentManager fragmentManager;

    public RankingAdapter(ArrayList<Ranking> rankings){
        this.rankings = rankings;
    }



    // Le view holder qui contient toutes les infos
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleView rank;
        public TextView person;
        public TextView result;
        public ImageButton more;


        public ViewHolder(View itemView) {
            super(itemView);

            rank = (CircleView) itemView.findViewById(R.id.adapter_list_avatar_avatar);
            person = (TextView) itemView.findViewById(R.id.adapter_list_avatar_first);
            result = (TextView) itemView.findViewById(R.id.adapter_list_avatar_second);
            more = (ImageButton) itemView.findViewById(R.id.adapter_list_avatar_more);
        }
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

        return new ViewHolder(view);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(RankingAdapter.ViewHolder holder, int position) {

        Ranking ranking = rankings.get(position);

        String rank = ranking.getRank();
        String person = ranking.getPerson();
        String result = ranking.getResult();
        String details = ranking.getDetails();

        holder.rank.setText(rank);
        holder.person.setText(person);

        if(isSingle) {
            holder.result.setText(result);
        }

        else {

            Assets.formatHtmlAverageDetails(result, details);

        }

        loadMenu(holder, ranking);

    }

    @Override
    public int getItemCount() {
        return rankings.size();
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    private void loadMenu(RankingAdapter.ViewHolder holder, final Ranking ranking) {
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.menu_pop_ranking);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.menu_pop_details:
                                onDetails(fragmentManager, ranking);
                                return true;

                            case R.id.menu_pop_goto_history:
                                gotoHistory(view.getContext());
                                return true;

                            case R.id.menu_pop_goto_records:
                                gotoRecords(view.getContext());
                                return true;

                            case R.id.menu_pop_share:
                                onShare(view.getContext(), ranking);
                                return true;
                        }

                        return false;
                    }
                });
                popupMenu.show();

            }
        });
    }

    private void gotoHistory(Context context) {

    }

    private void gotoRecords(Context context) {

    }

    private void onDetails(FragmentManager fragmentManager, Ranking ranking) {

        if(fragmentManager != null) {

            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.EXTRAS.RANKING, ranking);

            DialogFragment rankingDialog = new RankingDetailsDialog();

            rankingDialog.setArguments(bundle);

            rankingDialog.show(fragmentManager, "grqejcnrekqj");


        }

    }

    private void onShare(Context context, Ranking ranking) {
        String shareFormat = context.getString(R.string.share_ranking);

        String text = String.format(shareFormat, ranking.getPerson(), ranking.getRank(), ranking.getResult());

        String html = String.format(shareFormat, Assets.wrapStrong(ranking.getPerson()),
                Assets.wrapStrong(ranking.getRank()), Assets.wrapStrong(ranking.getResult()));


        Assets.shareIntent(context, text, html);
    }


}
