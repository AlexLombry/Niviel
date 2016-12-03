package com.adrastel.niviel.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String WCA_ID = "wca_id";
    public static final String ID = "id";
    public static final String USERNAME = "username";
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

            // Les dimensions et stats sont définies globales son définies sur le ProfileFragment
        }

        return tracker;
    }

    public static AdRequest getAdRequest() {
        return new AdRequest.Builder()// todo: virer le test device
                .addTestDevice("60C90B1288225E9B7FDB8AB3972CC7E5")
                .setGender(AdRequest.GENDER_MALE)
                .build();
    }

    /**
     * Protège la fonction contre le crash
     * @param intent Activité
     */
    @Override
    public void startActivity(Intent intent) {
        if(intent.resolveActivity(getPackageManager()) != null)
            super.startActivity(intent);

        else
            Toast.makeText(this, R.string.error_activity, Toast.LENGTH_LONG).show();

    }
}
