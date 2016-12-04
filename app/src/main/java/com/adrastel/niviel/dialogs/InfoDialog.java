package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.adrastel.niviel.R;

public class InfoDialog extends DialogFragment {

    private static final String TITLE = "title";
    private static final String BODY = "body";

    public static InfoDialog newInstance(@StringRes int title, @StringRes int body) {

        Bundle args = new Bundle();
        args.putInt(TITLE, title);
        args.putInt(BODY, body);

        InfoDialog instance = new InfoDialog();
        instance.setArguments(args);
        return instance;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();

        int title = args.getInt(TITLE, 0);
        int body = args.getInt(BODY, 0);

        return new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton(R.string.ok, null)
                .create();
    }
}
