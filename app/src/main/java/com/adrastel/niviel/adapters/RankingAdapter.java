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
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.models.Ranking;
import com.adrastel.niviel.views.CircleView;

import java.util.ArrayList;

public class RankingAdapter extends BaseAdapter<RankingAdapter.ViewHolder> {

    private ArrayList<Ranking> rankings;

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


    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankingAdapter.ViewHolder holder, int position) {

        Ranking ranking = rankings.get(position);

        String rank = ranking.getRank();
        String person = ranking.getPerson();
        String result = ranking.getResult();

        holder.rank.setText(rank);
        holder.person.setText(person);
        holder.result.setText(result);

        loadMenu(holder, ranking);

    }

    @Override
    public int getItemCount() {
        return rankings.size();
    }

    private void loadMenu(RankingAdapter.ViewHolder holder, final Ranking ranking) {
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.menu_pop_list);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.menu_pop_history_details:
                                //onDetails(fragmentManager, ranking);
                                return true;

                            case R.id.menu_pop_history_share:
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

    private void onShare(Context context, Ranking ranking) {
        String shareFormat = context.getString(R.string.share_ranking);

        String text = String.format(shareFormat, ranking.getPerson(), ranking.getRank(), ranking.getResult());

        String html = String.format(shareFormat, Assets.wrapStrong(ranking.getPerson()),
                Assets.wrapStrong(ranking.getRank()), Assets.wrapStrong(ranking.getResult()));


        Assets.shareIntent(context, text, html);
    }


}
