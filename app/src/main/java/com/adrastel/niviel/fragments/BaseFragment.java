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
import android.view.View;

import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.interfaces.ActivityTunnelInterface;

public abstract class BaseFragment extends Fragment {
    public abstract int getStyle();

    protected ActivityTunnelInterface activityTunnelInterface;
    protected Snackbar snackbar;
    protected ConnectivityManager connectivityManager;
    protected SharedPreferences preferences;

    protected boolean alreadySelected = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        try {
            activityTunnelInterface = (ActivityTunnelInterface) getActivity();
        }

        catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    protected Snackbar makeSnackbar(@StringRes int resId, int duration) {
        if(activityTunnelInterface != null && activityTunnelInterface.getCoordinatorLayout() != null) {
            snackbar = Snackbar.make(activityTunnelInterface.getCoordinatorLayout(), resId, duration);
            return snackbar;
        }

        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(snackbar != null && snackbar.isShownOrQueued()) {
            snackbar.dismiss();
        }
    }

    public void onTabSelectedFirst() {
        Log.d("FIRST");
    }

    protected boolean isConnected() {
        return Assets.isConnected(connectivityManager);
    }

    public void onFabClick(View view) {}

}
