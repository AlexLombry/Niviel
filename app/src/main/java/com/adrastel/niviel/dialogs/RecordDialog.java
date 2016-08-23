package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.models.readable.Record;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecordDialog extends DialogFragment {

    Unbinder unbinder;
    @BindView(R.id.single) TextView single;
    @BindView(R.id.single_nr) TextView nr_single;
    @BindView(R.id.single_cr) TextView cr_single;
    @BindView(R.id.single_wr) TextView wr_single;

    @BindView(R.id.average) TextView average;
    @BindView(R.id.average_nr) TextView nr_average;
    @BindView(R.id.average_cr) TextView cr_average;
    @BindView(R.id.average_wr) TextView wr_average;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Record record = getArguments().getParcelable(Constants.EXTRAS.RECORDS);

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View view = inflater.inflate(R.layout.dialog_record_info, null);
        unbinder = ButterKnife.bind(this, view);


        if(record != null) {
            single.setText(record.getSingle());
            nr_single.setText(record.getNr_single());
            cr_single.setText(record.getCr_single());
            wr_single.setText(record.getWr_single());

            average.setText(record.getAverage());
            nr_average.setText(record.getNr_average());
            cr_average.setText(record.getCr_average());
            wr_average.setText(record.getWr_average());
        }

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.more_info);

        builder.setView(view);

        builder.setPositiveButton(R.string.ok, null);

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
