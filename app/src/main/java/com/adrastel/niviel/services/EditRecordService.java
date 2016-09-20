package com.adrastel.niviel.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.providers.html.RecordProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditRecordService extends IntentService {


    public static final String WCA_ID = "wca_id";
    public static final String USERNAME = "username";
    public static final String ACTION = "action";
    public static final int ADD_RECORD = 0;
    public static final int DELETE_RECORD = 1;

    private String wca_id = null;
    private String username = null;
    private ArrayList<Record> records;
    private DatabaseHelper db;
    private Handler handler;

    // Receiver
    public static final String INTENT_FILTER = "editrecordservice";
    public static final int ADD_RECORD_SUCCESS = 0;
    public static final int ADD_RECORD_FAILURE = 1;


    public EditRecordService() {
        super("EditRecordService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        db = DatabaseHelper.getInstance(this);
        handler = new Handler(Looper.getMainLooper());

        int action = intent.getIntExtra(ACTION, ADD_RECORD);

        wca_id = intent.getStringExtra(WCA_ID);
        username = intent.getStringExtra(USERNAME);

        if(action == ADD_RECORD) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.toast_adding, Toast.LENGTH_LONG).show();
                }
            });

            getRecords(new recordsCallback() {
                @Override
                public void onSuccess(ArrayList<Record> records) {
                    final long follower = db.insertFollower(username, wca_id, System.currentTimeMillis());
                    Log.d(String.valueOf(follower));
                    insertRecords(follower, records);
                }
            });
        }

        else if(action == DELETE_RECORD) {

            long follower = db.getFollowerIdFromWca(wca_id);

            Log.d("Number before", String.valueOf(db.selectRecordsFromFollower(follower).size()));

            db.deleteRecords(follower);

            Log.d("Number after", String.valueOf(db.selectRecordsFromFollower(follower).size()));
            db.deleteFollower(wca_id);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    String confirmation = String.format(getString(R.string.toast_unfollow_confirmation), username);
                    Toast.makeText(getApplicationContext(), confirmation, Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("destroy service");
    }

    private void getRecords(final recordsCallback callback) {

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(Constants.WCA.HOST)
                .addEncodedPathSegments("results/p.php")
                .addEncodedQueryParameter("i", wca_id)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.error_connection, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(INTENT_FILTER);
                        intent.putExtra(ACTION, ADD_RECORD_FAILURE);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.error_connection, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(INTENT_FILTER);
                            intent.putExtra(ACTION, ADD_RECORD_FAILURE);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        }
                    });
                    return;
                }

                Document document = Jsoup.parse(response.body().string());
                records = RecordProvider.getRecord(getApplicationContext(), document);
                response.close();

                callback.onSuccess(records);


            }
        });
    }

    private interface recordsCallback {
        void onSuccess(ArrayList<Record> records);
    }

    private void insertRecords(long follower, ArrayList<Record> records) {


        int s = records.size();

        String[] events = new String[s];
        long[] singles = new long[s];
        long[] nr_singles = new long[s];
        long[] cr_singles = new long[s];
        long[] wr_singles = new long[s];
        long[] averages = new long[s];
        long[] nr_average = new long[s];
        long[] cr_average = new long[s];
        long[] wr_average = new long[s];

        // Il y a 3 try/catch parce que certains records sont en single ou averages ou les 2
        for(int i = 0; i < s; i++) {

            Record record = records.get(i);

            // Event
            try {
                events[i] = record.getEvent();
            }

            catch (Exception e) {
                e.printStackTrace();
            }

            try {
                singles[i] = Assets.minSecToSec(record.getSingle());
                nr_singles[i] = Long.parseLong(record.getNr_single());
                cr_singles[i] = Long.parseLong(record.getCr_single());
                wr_singles[i] = Long.parseLong(record.getWr_single());
            }

            catch (Exception e) {
                Log.e(events[i]);
                singles[i] = 0;
                nr_singles[i] = 0;
                cr_singles[i] = 0;
                wr_singles[i] = 0;

                e.printStackTrace();
            }

            try {
                averages[i] = Assets.minSecToSec(record.getAverage());
                nr_average[i] = Long.parseLong(record.getNr_average());
                cr_average[i] = Long.parseLong(record.getCr_average());
                wr_average[i] = Long.parseLong(record.getWr_average());
            }

            catch (Exception e) {
                Log.e(events[i]);
                averages[i] = 0;
                nr_average[i] = 0;
                nr_average[i] = 0;
                nr_average[i] = 0;

                e.printStackTrace();
            }

            db.insertRecord(
                    follower, events[i],
                    singles[i], nr_singles[i], cr_singles[i], wr_singles[i],
                    averages[i], nr_average[i], cr_average[i], wr_average[i]
            );



        }


        handler.post(new Runnable() {
            @Override
            public void run() {
                String confirmation = String.format(getString(R.string.toast_follow_confirmation), username);
                Toast.makeText(getApplicationContext(), confirmation, Toast.LENGTH_LONG).show();


                Intent intent = new Intent(INTENT_FILTER);
                intent.putExtra(ACTION, ADD_RECORD_SUCCESS);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });
    }
}