package com.adrastel.niviel.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.adrastel.niviel.R;

public class NotificationActivity extends Activity {

    public static final String CONTENT = "content";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notification);

        String content = getIntent().getStringExtra(CONTENT);

        TextView viewContent = (TextView) findViewById(R.id.content);

        viewContent.setText(content);

    }
}
