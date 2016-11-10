package com.adrastel.niviel.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.adrastel.niviel.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public abstract class BaseActivity extends AppCompatActivity {

    private Tracker tracker;

    public void setDayNightTheme(boolean isDark) {
        if(isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    /**
     * Retourne le tracker pour Google Analytics
     */
    public synchronized Tracker getDefaultTracker() {
        if(tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(R.xml.global_tracker);

            tracker.setAnonymizeIp(false);
            tracker.enableExceptionReporting(true);
        }

        return tracker;
    }

}
