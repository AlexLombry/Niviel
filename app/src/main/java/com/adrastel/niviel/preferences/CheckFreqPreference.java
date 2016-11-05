package com.adrastel.niviel.preferences;

import android.content.Context;
import android.content.Intent;
import android.preference.ListPreference;
import android.util.AttributeSet;

import com.adrastel.niviel.services.CheckRecordService;

public class CheckFreqPreference extends ListPreference {

    public CheckFreqPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        if(positiveResult) {

            Intent checkRecords = new Intent(getContext(), CheckRecordService.class);
            getContext().startService(checkRecords);

        }
    }
}