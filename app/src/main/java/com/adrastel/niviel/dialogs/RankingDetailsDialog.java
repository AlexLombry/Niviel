package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.DetailsMaker;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.models.readable.Ranking;

public class RankingDetailsDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Ranking ranking = getArguments().getParcelable(Constants.EXTRAS.RANKING);


        DetailsMaker detailsMaker = new DetailsMaker(getContext());

        if(ranking != null) {

            detailsMaker.add(R.string.rank, ranking.getRank());
            detailsMaker.add(R.string.person, ranking.getPerson());
            detailsMaker.add(R.string.result, ranking.getResult());
            detailsMaker.add(R.string.citizen, ranking.getCitizen());
            detailsMaker.add(R.string.competition, ranking.getCompetition());

            if(!ranking.getDetails().equals(" ")) {
                detailsMaker.add(R.string.details, ranking.getDetails());
            }

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.ranking);
        builder.setMessage(detailsMaker.build());

        builder.setPositiveButton(R.string.ok, null);

        return builder.create();
    }
}
