package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.DetailsMaker;
import com.adrastel.niviel.models.readable.History;

public class HistoryDialog extends DialogFragment {


    public static HistoryDialog newInstance(History history) {

        Bundle args = new Bundle();
        args.putParcelable(Constants.EXTRAS.HISTORY, history);

        HistoryDialog fragment = new HistoryDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        History history = bundle.getParcelable(Constants.EXTRAS.HISTORY);

        DetailsMaker dialogMaker = new DetailsMaker(getContext());

        if(history != null) {
            dialogMaker.add(R.string.event, history.getEvent());
            dialogMaker.add(R.string.competition, history.getCompetition());
            dialogMaker.add(R.string.round, history.getRound());
            dialogMaker.add(R.string.rank, history.getPlace());
            dialogMaker.add(R.string.best, history.getBest());
            dialogMaker.add(R.string.average, history.getAverage());
            dialogMaker.add(R.string.details, history.getResult_details());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.details);

        builder.setMessage(dialogMaker.build());


        builder.setPositiveButton(R.string.ok, null);

        return builder.create();
    }
}
