package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.models.History;

public class HistoryDialog extends DialogFragment {



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        History history = bundle.getParcelable(Constants.TAG.HISTORY);

        // on crée la vue
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View view = inflater.inflate(R.layout.dialog_history_details, null);

        TextView event = (TextView) view.findViewById(R.id.dialog_history_details_event);
        TextView competition = (TextView) view.findViewById(R.id.dialog_history_details_competition);
        TextView round = (TextView) view.findViewById(R.id.dialog_history_details_round);
        TextView best = (TextView) view.findViewById(R.id.dialog_history_details_best);
        TextView average = (TextView) view.findViewById(R.id.dialog_history_details_average);
        TextView results = (TextView) view.findViewById(R.id.dialog_history_details_results);

        if(history != null) {
            event.setText(history.getEvent());
            competition.setText(history.getCompetition());
            round.setText(history.getRound());
            best.setText(history.getBest());
            average.setText(history.getAverage());
            results.setText(history.getResult_details());
        }

        // puis la boite de dialogue

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.details);

        builder.setView(view);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }
}
