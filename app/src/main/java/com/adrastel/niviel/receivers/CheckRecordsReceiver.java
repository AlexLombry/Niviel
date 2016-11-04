package com.adrastel.niviel.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.adrastel.niviel.BuildConfig;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.services.CheckRecordService;

import java.util.Calendar;

public class CheckRecordsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("init receiver");

        Intent checkRecords = new Intent(context, CheckRecordService.class);
        context.startService(checkRecords);


    }
}