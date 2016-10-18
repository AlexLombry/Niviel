package com.adrastel.niviel.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.services.CheckRecordService;

import java.util.Calendar;

public class CheckRecordsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("init receiver");

        Intent checkRecords = new Intent(context, CheckRecordService.class);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, checkRecords, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 18);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        context.startService(checkRecords);


    }
}
