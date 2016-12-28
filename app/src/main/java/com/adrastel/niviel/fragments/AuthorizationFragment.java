package com.adrastel.niviel.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.adrastel.niviel.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AuthorizationFragment extends Fragment {

    Unbinder unbinder;

    @BindView(R.id.checkbox) CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_authorization, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean send_data = preferences.getBoolean(getString(R.string.pref_send_data), true);

        checkBox.setChecked(send_data);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                preferences.edit()
                        .putBoolean(getString(R.string.pref_send_data), b)
                        .apply();

            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
