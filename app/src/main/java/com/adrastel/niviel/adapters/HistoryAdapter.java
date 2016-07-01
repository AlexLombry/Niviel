package com.adrastel.niviel.adapters;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adrastel.niviel.Models.History;
import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.BackgroundCards;

import java.util.ArrayList;

/**
 * HistoryAdapter organise les donnes données par un objet histor
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<History> histories;
    private BackgroundCards backgroundCards = new BackgroundCards(4164);
    private ArrayList<Integer> colors = backgroundCards.shuffle();

    // Le view holder qui contient toutes les infos
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView competition;
        public TextView round;
        public TextView place;
        public TextView best;
        public TextView average;
        public TextView result_details;
        public CardView card;
        public LinearLayout expend_layout;
        public Button expend_button;
        public Button share_button;

        public ViewHolder(View itemView) {
            super(itemView);

            competition = (TextView) itemView.findViewById(R.id.adapter_history_competition);
            round = (TextView) itemView.findViewById(R.id.adapter_history_round);
            place = (TextView) itemView.findViewById(R.id.adapter_history_place);
            best = (TextView) itemView.findViewById(R.id.adapter_history_best);
            average = (TextView) itemView.findViewById(R.id.adapter_history_average);
            result_details = (TextView) itemView.findViewById(R.id.adapter_history_result_details);
            card = (CardView) itemView.findViewById(R.id.adapter_history_card);
            expend_layout = (LinearLayout) itemView.findViewById(R.id.adapter_history_expend_layout);
            expend_button = (Button) itemView.findViewById(R.id.adapter_history_expend_button);
            share_button = (Button) itemView.findViewById(R.id.adapter_history_share_button);
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

        final String competition = history.getCompetition();
        String round = history.getRound();
        String place = history.getPlace();
        String best = history.getBest();
        String average = history.getAverage();
        String result_details = history.getResult_details();

        holder.competition.setText(competition);
        holder.round.setText(round);
        holder.place.setText(place);
        holder.best.setText(best);
        holder.average.setText(average);
        holder.result_details.setText(result_details);

        // background
        int color_position = backgroundCards.get(position);
        int color = colors.get(color_position);


        holder.card.setCardBackgroundColor(color);




        // listeners

        holder.expend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.expend_layout.getVisibility() == View.VISIBLE) {
                    holder.expend_layout.setVisibility(View.GONE);
                }

                else {
                    holder.expend_layout.setVisibility(View.VISIBLE);
                }
            }
        });


        holder.share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = "Je suis arrivé à place n°" + history.getPlace() +
                        " à la compétition " + history.getCompetition() +
                        " en faisant un temps moyen de " + history.getAverage();
                String html = "Je suis allé à la compétition <strong>" + history.getCompetition()  +
                        "</strong>. J'ai fait en moyenne de <strong>" + history.getAverage() +
                        "</strong> et mon meilleur temps est <strong>" + history.getBest() + "</strong>.";

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, text);
                intent.putExtra(Intent.EXTRA_HTML_TEXT, html);
                intent.setType("text/plain");

                view.getContext().startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return histories.size();
    }


}
