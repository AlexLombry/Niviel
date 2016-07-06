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
import com.adrastel.niviel.assets.BackgroundCards;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.dialogs.HistoryDialog;
import com.adrastel.niviel.models.History;
import com.adrastel.niviel.views.CircleView;

import java.util.ArrayList;

/**
 * HistoryAdapter organise les donnes données par un objet histor
 */
public class HistoryAdapter extends BaseAdapter<HistoryAdapter.ViewHolder> {

    private ArrayList<History> histories;
    private BackgroundCards backgroundCards = new BackgroundCards(4164);
    private ArrayList<Integer> colors = backgroundCards.shuffle();
    private FragmentManager fragmentManager;


    // Le view holder qui contient toutes les infos
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleView place;
        public TextView competition;
        public TextView result_details;
        public ImageButton more;


        public ViewHolder(View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.adapter_list_avatar_avatar);
            competition = (TextView) itemView.findViewById(R.id.adapter_list_avatar_first);
            result_details = (TextView) itemView.findViewById(R.id.adapter_list_avatar_second);
            more = (ImageButton) itemView.findViewById(R.id.adapter_list_avatar_more);
        }
    }

    // Constructeur
    public HistoryAdapter(ArrayList<History> histories) {
        this.histories = histories;
    }

    // Lors de la creation de la vue
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_list_avatar, parent, false);

        return new ViewHolder(view);
    }

    // Lors de l'hydratation de la vue
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final History history = histories.get(position);

        String competition = history.getCompetition();
        String place = history.getPlace();
        String result_details = history.getResult_details();

        holder.competition.setText(competition);
        holder.place.setText(place);
        holder.result_details.setText(result_details);

        loadMenu(holder, history);


    }

    @Override
    public int getItemCount() {
        return histories.size();
    }


    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private void loadMenu(ViewHolder holder, final History history) {
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
                                onDetails(fragmentManager, history);
                                return true;

                            case R.id.menu_pop_history_share:
                                onShare(view.getContext(), history);
                                return true;
                        }

                        return false;
                    }
                });
                popupMenu.show();

            }
        });
    }

    private void onDetails(FragmentManager fragmentManager, History history) {

        if(fragmentManager != null) {



            Bundle bundle = new Bundle();
            bundle.putInt("test", 2);
            bundle.putSerializable(Constants.EXTRAS.HISTORY, history);

            DialogFragment historyDialog = new HistoryDialog();

            historyDialog.setArguments(bundle);

            historyDialog.show(fragmentManager, Constants.TAG.HISTORY);

        }

    }

    private void onShare(Context context, History history) {


        String shareFormat = context.getString(R.string.share_history);

        String text = String.format(shareFormat, history.getPlace(), history.getCompetition(), history.getAverage());

        String html = String.format(shareFormat, Assets.wrapStrong(history.getPlace()),
                Assets.wrapStrong(history.getCompetition()), Assets.wrapStrong(history.getAverage()));


        Assets.shareIntent(context, text, html);
    }

}