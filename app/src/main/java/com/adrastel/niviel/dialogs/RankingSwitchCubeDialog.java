package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;

public class RankingSwitchCubeDialog extends DialogFragment {

    RankingSwitchCubeListener listener;

    private int position = -1;

    private AlertDialog dialog;

    public static RankingSwitchCubeDialog newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(Constants.EXTRAS.CUBE_POSITION, position);

        RankingSwitchCubeDialog instance = new RankingSwitchCubeDialog();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(Constants.EXTRAS.CUBE_POSITION, -1);

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

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.switch_cube);
        builder.setIcon(R.drawable.ic_swap);

        builder.setSingleChoiceItems(R.array.cubes, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                position = i;
                manageButtons();
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

        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                manageButtons();
            }
        });

        return dialog;
    }

    private void manageButtons() {

        if(dialog != null) {

            if(position == 4 || position == 15 || position == 16 || position == 17) {
                dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.INVISIBLE);
            }

            else {
                dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.VISIBLE);
            }
        }
    }

    public interface RankingSwitchCubeListener {
        void onClick(int position, boolean isSingle);
    }
}
