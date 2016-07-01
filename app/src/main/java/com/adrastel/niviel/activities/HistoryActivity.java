package com.adrastel.niviel.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.adrastel.niviel.Models.History;
import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.HistoryAdapter;
import com.adrastel.niviel.assets.Constants;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Context context = getApplicationContext();

        Intent intent = getIntent();
        assert intent != null;

        ArrayList<History> histories = (ArrayList<History>) intent.getSerializableExtra(Constants.EXTRAS.RECORDS);
        assert histories != null;

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_history_recycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        HistoryAdapter adapter = new HistoryAdapter(histories);

        recyclerView.setAdapter(adapter);

    }

}
