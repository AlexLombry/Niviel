package com.adrastel.niviel.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.ActivityTunnelInterface;
import com.adrastel.niviel.assets.Assets;

public abstract class BaseFragment extends Fragment {
    public abstract int getTitle();
    public abstract int getPrimaryColor();
    public abstract int getPrimaryDarkColor();
    public abstract int getFabVisibility();
    public abstract int getFabIcon();
    public abstract void onFabClick(View view);

    protected ActivityTunnelInterface activityTunnelInterface;
    protected Snackbar snackbar;
    protected ConnectivityManager connectivityManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

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

    protected boolean isConnected() {
        return Assets.isConnected(connectivityManager);
    }
}
