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


        if(BuildConfig.DEBUG) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            int old_call = preferences.getInt("call_service", 0);
            old_call++;
            preferences.edit().putInt("call_service", old_call).apply();
        }

        Intent checkRecords = new Intent(context, CheckRecordService.class);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, checkRecords, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 18);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, AlarmManager.INTERVAL_DAY / 2, pendingIntent);

        context.startService(checkRecords);


    }
}
