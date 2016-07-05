package com.adrastel.niviel.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adrastel.niviel.Models.History;
import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.BackgroundCards;
import com.adrastel.niviel.views.CircleView;

import java.util.ArrayList;

/**
 * HistoryAdapter organise les donnes données par un objet histor
 */
public class HistoryAdapter extends BaseAdapter<HistoryAdapter.ViewHolder> {

    private ArrayList<History> histories;
    private BackgroundCards backgroundCards = new BackgroundCards(4164);
    private ArrayList<Integer> colors = backgroundCards.shuffle();

    // Le view holder qui contient toutes les infos
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleView place;
        public TextView competition;
        public TextView result_details;
        public ImageButton more;


        public ViewHolder(View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.adapter_history_place);
            competition = (TextView) itemView.findViewById(R.id.adapter_history_competition);
            result_details = (TextView) itemView.findViewById(R.id.adapter_history_result_details);
            more = (ImageButton) itemView.findViewById(R.id.adapter_history_more);
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

        View view = inflater.inflate(R.layout.adapter_history, parent, false);

        return new ViewHolder(view);
    }

    // Lors de l'hydratation de la vue
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final History history = histories.get(position);

        String competition = history.getCompetition();
        String place  = history.getPlace();
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

    private void loadMenu(ViewHolder holder, final History history) {
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.menu_pop_history);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.menu_pop_history_details:
                                onDetails(view.getContext(), history);
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

    private void onDetails(Context context, History history) {

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.dialog_history_details, null);

        TextView event = (TextView) view.findViewById(R.id.dialog_history_details_event);
        TextView competition = (TextView) view.findViewById(R.id.dialog_history_details_competition);
        TextView round = (TextView) view.findViewById(R.id.dialog_history_details_round);
        TextView best = (TextView) view.findViewById(R.id.dialog_history_details_best);
        TextView average = (TextView) view.findViewById(R.id.dialog_history_details_average);
        TextView results = (TextView) view.findViewById(R.id.dialog_history_details_results);

        event.setText(history.getEvent());
        competition.setText(history.getCompetition());
        round.setText(history.getRound());
        best.setText(history.getBest());
        average.setText(history.getAverage());
        results.setText(history.getResult_details());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.details));

        builder.setView(view);

        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();

    }

    private void onShare(Context context, History history) {


        String shareFormat = context.getString(R.string.share_history);

        String text = String.format(shareFormat, history.getPlace(), history.getCompetition(), history.getAverage());

        String html = String.format(shareFormat, Assets.wrapStrong(history.getPlace()),
                Assets.wrapStrong(history.getCompetition()), Assets.wrapStrong(history.getAverage()));


        Intent intent = new Intent();

        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, html);

        Intent chooser = Intent.createChooser(intent, context.getString(R.string.share));

        context.startActivity(chooser);
    }

}