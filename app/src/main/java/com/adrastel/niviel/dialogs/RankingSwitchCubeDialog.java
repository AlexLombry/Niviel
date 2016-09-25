package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;

public class RankingSwitchCubeDialog extends DialogFragment {

    RankingSwitchCubeListener listener;

    private int position = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(Constants.EXTRAS.POSITION, -1);

        try {
            listener = (RankingSwitchCubeListener) getTargetFragment();
        }

        catch (ClassCastException exception) {
            throw new ClassCastException("Calling fragment must implement RankingSwitchCubeListener interface");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.switch_cube);
        builder.setIcon(R.drawable.ic_swap);

        builder.setSingleChoiceItems(R.array.cubes, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                position = i;
            }
        });



        builder.setPositiveButton(R.string.single, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onClick(position, true);
            }
        });

        builder.setNeutralButton(R.string.average, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onClick(position, false);
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    public interface RankingSwitchCubeListener {
        void onClick(int position, boolean isSingle);
    }
}
