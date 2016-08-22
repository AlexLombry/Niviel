package com.adrastel.niviel.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

import com.adrastel.niviel.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        initPrefForEdit(R.string.pref_wca_id);


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        Preference preference = findPreference(s);

        updatePreference(preference);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initPrefForEdit(@StringRes int keyRes) {

        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
        String key = getString(keyRes);

        EditTextPreference preference = (EditTextPreference) findPreference(key);

        preference.setSummary(preferences.getString(key, ""));

    }

    private void updatePreference(Preference p) {

        if(p instanceof EditTextPreference) {
            EditTextPreference edit = (EditTextPreference) p;

            edit.setSummary(edit.getText());
        }

    }
}
