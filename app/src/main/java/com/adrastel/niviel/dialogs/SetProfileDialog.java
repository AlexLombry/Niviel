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
import com.adrastel.niviel.assets.Log;

public class SetProfileDialog extends DialogFragment {

    DialogProfileListener listener;
    String username = null;

    public static SetProfileDialog getInstance(String name) {
        SetProfileDialog instance = new SetProfileDialog();

        Bundle args = new Bundle();
        args.putString(Constants.EXTRAS.USERNAME, name);

        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString(Constants.EXTRAS.USERNAME, null);
        }

        try {
            listener = (DialogProfileListener) getTargetFragment();
        }

        catch (ClassCastException e) {
            throw new ClassCastException("Error on SetProfileDialog");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_edit_profile_title);
        builder.setMessage(String.format(getString(R.string.dialog_edit_profile_message), username));

        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onConfirm();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    public interface DialogProfileListener {
        void onConfirm();
    }
}
