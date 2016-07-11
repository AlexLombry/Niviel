package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.DetailsDialogMaker;
import com.adrastel.niviel.models.Ranking;

public class RankingDetailsDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Ranking ranking = getArguments().getParcelable(Constants.EXTRAS.RANKING);


        DetailsDialogMaker detailsDialogMaker = new DetailsDialogMaker(getContext());

        if(ranking != null) {

            detailsDialogMaker.add(R.string.rank, ranking.getRank());
            detailsDialogMaker.add(R.string.person, ranking.getPerson());
            detailsDialogMaker.add(R.string.result, ranking.getResult());
            detailsDialogMaker.add(R.string.citizen, ranking.getCitizen());
            detailsDialogMaker.add(R.string.competition, ranking.getCompetition());
            detailsDialogMaker.add(R.string.details, ranking.getDetails());

        }
        // todo: ajouter le layout dialog_ranking_details
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.ranking);
        builder.setMessage(detailsDialogMaker.build());

        builder.setPositiveButton(R.string.ok, null);

        return builder.create();
    }
}
