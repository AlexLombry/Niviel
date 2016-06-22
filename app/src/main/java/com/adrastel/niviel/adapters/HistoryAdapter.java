package com.adrastel.niviel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adrastel.niviel.Models.History;
import com.adrastel.niviel.R;

import java.util.ArrayList;

/**
 * HistoryAdapter organise les donnes données par un objet histor
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<History> histories;

    // Le view holder qui contient toutes les infos
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView competition;
        public TextView round;
        public TextView place;
        public TextView best;
        public TextView average;
        public TextView result_details;

        public ViewHolder(View itemView) {
            super(itemView);

            competition = (TextView) itemView.findViewById(R.id.adapter_history_competition);
            round = (TextView) itemView.findViewById(R.id.adapter_history_round);
            place = (TextView) itemView.findViewById(R.id.adapter_history_place);
            best = (TextView) itemView.findViewById(R.id.adapter_history_best);
            average = (TextView) itemView.findViewById(R.id.adapter_history_average);
            result_details = (TextView) itemView.findViewById(R.id.adapter_history_result_details);

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
    public void onBindViewHolder(ViewHolder holder, int position) {

        History history = histories.get(position);

        String competition = history.getCompetition();
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

    }

    @Override
    public int getItemCount() {
        return histories.size();
    }


}
