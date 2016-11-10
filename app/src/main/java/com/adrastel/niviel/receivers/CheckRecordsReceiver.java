package com.adrastel.niviel.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.services.CheckRecordService;
import com.google.android.gms.analytics.HitBuilders;

public class CheckRecordsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("init receiver");

        Intent checkRecords = new Intent(context, CheckRecordService.class);
        context.startService(checkRecords);
    }
}