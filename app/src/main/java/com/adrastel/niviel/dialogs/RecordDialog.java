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
import com.adrastel.niviel.models.Record;

public class RecordDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Record record = getArguments().getParcelable(Constants.EXTRAS.RECORDS);

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View view = inflater.inflate(R.layout.dialog_record_info, null);

        TextView single = (TextView) view.findViewById(R.id.dialog_personal_record_info_single);
        TextView nr_single = (TextView) view.findViewById(R.id.dialog_personal_record_info_single_nr);
        TextView cr_single = (TextView) view.findViewById(R.id.dialog_personal_record_info_single_cr);
        TextView wr_single = (TextView) view.findViewById(R.id.dialog_personal_record_info_single_wr);

        TextView average = (TextView) view.findViewById(R.id.dialog_personal_record_info_average);
        TextView nr_average = (TextView) view.findViewById(R.id.dialog_personal_record_info_average_nr);
        TextView cr_average = (TextView) view.findViewById(R.id.dialog_personal_record_info_average_cr);
        TextView wr_average = (TextView) view.findViewById(R.id.dialog_personal_record_info_average_wr);

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

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }
}
