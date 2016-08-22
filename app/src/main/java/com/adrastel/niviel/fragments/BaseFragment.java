package com.adrastel.niviel.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

public abstract class BaseFragment extends Fragment {
    public abstract int getTitle();
    public abstract int getPrimaryColor();
    public abstract int getPrimaryDarkColor();
    public abstract int getFabVisibility();
    public abstract int getFabIcon();
    public abstract void onFabClick(View view);
}
