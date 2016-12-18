package com.adrastel.niviel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.models.writeable.OldNewRecord;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    public static final String OLD_NEW_RECORDS = "oldnewrecords";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);

        TextView viewContent = (TextView) findViewById(R.id.content);

        ArrayList<OldNewRecord> records = getIntent().getParcelableArrayListExtra(OLD_NEW_RECORDS);

        if(records != null) {
            viewContent.setText(records.get(0).getName());
        }
    }
}
