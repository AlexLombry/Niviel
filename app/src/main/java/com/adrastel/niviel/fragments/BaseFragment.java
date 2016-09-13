package com.adrastel.niviel.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Log;

public abstract class BaseFragment extends Fragment {
    public abstract int getStyle();

    private Snackbar snackbar;
    protected ConnectivityManager connectivityManager;
    protected SharedPreferences preferences;
    protected MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        try {
            activity = (MainActivity) getActivity();
        }

        catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    protected Snackbar makeSnackbar(@StringRes int resId, int duration) {
        if(activity != null && activity.getCoordinatorLayout() != null) {
            snackbar = Snackbar.make(activity.getCoordinatorLayout(), resId, duration);
            return snackbar;
        }

        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(snackbar != null) {
            snackbar.dismiss();
        }
    }

    protected boolean isConnected() {
        return Assets.isConnected(connectivityManager);
    }

}
