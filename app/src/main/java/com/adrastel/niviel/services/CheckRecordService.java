package com.adrastel.niviel.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.NotificationCompat;

import com.adrastel.niviel.BuildConfig;
import com.adrastel.niviel.R;
import com.adrastel.niviel.RecordModel;
import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.activities.SettingsActivity;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.InboxStyle;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.database.Record;
import com.adrastel.niviel.fragments.ProfileFragment;
import com.adrastel.niviel.models.writeable.BufferRecord;
import com.adrastel.niviel.providers.html.RecordProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckRecordService extends IntentService {

    DatabaseHelper database;

    private static int notif_id = 0;


    /**
     * Stocke les messages des records pour les afficher dans la notification
     */
    private LongSparseArray<String> notifMsgs = new LongSparseArray<>();

    public CheckRecordService() {
        super("checkRecordService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = DatabaseHelper.getInstance(this);
        Log.i("Create CheckRecordService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ArrayList<Follower> followers = database.selectAllFollowers();

        for(final Follower follower : followers) {

            long follower_id = follower._id();

            final ArrayList<Record> oldRecords = database.selectRecordsFromFollower(follower_id);

            callData(follower.wca_id(), new dataCallback() {
                @Override
                public void onSuccess(ArrayList<com.adrastel.niviel.models.readable.Record> newRecords) {

                    compareRecords(follower, oldRecords, newRecords);

                }
            });

        }
    }

    private void compareRecords(Follower follower, ArrayList<Record> oldRecords, ArrayList<com.adrastel.niviel.models.readable.Record> newRecords) {

        // si la taille est la meme et que l'event est le meme
        Log.d(String.valueOf(oldRecords.size()) + "<->" + String.valueOf(newRecords.size()));

        try {
            Collections.sort(oldRecords, new Record.Comparator());
            Collections.sort(newRecords, new com.adrastel.niviel.models.readable.Record.Comparator());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if(oldRecords.size() == newRecords.size()) {

            for(int i = 0; i < oldRecords.size(); i++) {

                Record oldRecord = oldRecords.get(i);
                com.adrastel.niviel.models.readable.Record newRecord = newRecords.get(i);

                // Si ils ont le meme event, on peut comparer
                if(oldRecord.event().equals(newRecord.getEvent())) {

                    try {

                        // Check for singles

                        // Savoir si il faut notifier
                        boolean hasToNotify = false;

                        // Corps de la notification
                        String content = "";
                        String nr = "";
                        String cr = "";
                        String wr = "";

                        // Corps de la notification etendue
                        InboxStyle inboxStyle = new InboxStyle(this);

                        RecordModel.Marshal values = Record.FACTORY.marshal();

                        if(singleChanged(oldRecord, newRecord)) {

                            Log.v(oldRecord.single() + "->" + newRecord.getSingle());


                            content = String.format(getString(R.string.notif_new_single), newRecord.getSingle());

                            // Update notification title
                            nr = newRecord.getNr_single();
                            cr = newRecord.getCr_single();
                            wr = newRecord.getWr_single();

                            // Update database
                            values.single(newRecord.getSingle());
                            values.nr_single(Long.parseLong(newRecord.getNr_single()));
                            values.cr_single(Long.parseLong(newRecord.getCr_single()));
                            values.wr_single(Long.parseLong(newRecord.getWr_single()));

                            hasToNotify = true;

                        }

                        // Check for average
                        if(averageChanged(oldRecord, newRecord)) {

                            Log.v(oldRecord.average() + "->" + newRecord.getAverage());
                            // Si le titre est vide, on ne fait rien, sinon on ajoute un séparateur
                            content = content.equals("") ? "" : content + " | ";
                            content += String.format(getString(R.string.notif_new_average), newRecord.getAverage());

                            // Update notification
                            nr = nr.equals("") ? "" : nr + " | ";
                            nr += newRecord.getNr_average();

                            cr = cr.equals("") ? "" : cr + " | ";
                            cr += newRecord.getCr_average();

                            wr = wr.equals("") ? "" : wr + " | ";
                            wr += newRecord.getWr_average();

                            // Update database
                            values.average(newRecord.getAverage());
                            values.nr_average(Long.parseLong(newRecord.getNr_average()));
                            values.cr_average(Long.parseLong(newRecord.getCr_average()));
                            values.wr_average(Long.parseLong(newRecord.getWr_average()));

                            hasToNotify = true;
                        }

                        if (hasToNotify) {

                            inboxStyle.setBigContentTitle(follower.name());
                            inboxStyle.setSummaryText(follower.wca_id());
                            inboxStyle.addLine(content);
                            inboxStyle.addLine(String.format(getString(R.string.record_nr), nr));
                            inboxStyle.addLine(String.format(getString(R.string.record_cr), cr));
                            inboxStyle.addLine(String.format(getString(R.string.record_wr), wr));

                            makeNotification(follower, follower.name(), content, inboxStyle);

                            // Update database
                            database.updateRecord(follower._id(), newRecord.getEvent(), values.asContentValues());
                        }
                    }

                    catch (Exception e) {
                        Log.e("Invalid long");
                    }
                }
            }

        }

        // Si il y a un nouvel event
        else if (oldRecords.size() < newRecords.size()) {

            ArrayList<com.adrastel.niviel.models.readable.Record> filtredNewRecords;

            try {
                filtredNewRecords = Assets.getNewRecords(oldRecords, newRecords);


                for (com.adrastel.niviel.models.readable.Record record : filtredNewRecords) {

                    long Snr, Scr, Swr, Anr, Acr, Awr;

                    try {
                        Snr = Long.parseLong(record.getNr_single());
                        Scr = Long.parseLong(record.getCr_single());
                        Swr = Long.parseLong(record.getWr_single());
                    }

                    catch (Exception e) {
                        Snr = 0;
                        Scr = 0;
                        Swr = 0;
                    }

                    try {
                        Anr = Long.parseLong(record.getNr_single());
                        Acr = Long.parseLong(record.getCr_single());
                        Awr = Long.parseLong(record.getWr_single());
                    }

                    catch (Exception e) {
                        Anr = 0;
                        Acr = 0;
                        Awr = 0;
                    }

                    database.insertRecord(follower._id(), record.getEvent(), record.getSingle(), Snr, Scr, Swr, record.getAverage(), Anr, Acr, Awr);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            // Les nouveaux record en locals
            ArrayList<Record> oldFollowersUpdated = database.selectRecordsFromFollower(follower._id());

            // Si l'ajout a bien été effectué, appelle la fonction de nouveau
            if(oldFollowersUpdated.size() == newRecords.size()) {
                compareRecords(follower, oldFollowersUpdated, newRecords);
            }
        }

    }

    private void makeNotification(Follower follower, String title, String content, InboxStyle bigContent) {

        // Voir le profil
        Intent gotoMainActivity = new Intent(this, MainActivity.class);
        gotoMainActivity.putExtra(Constants.EXTRAS.FRAGMENT, ProfileFragment.FRAGMENT_TAG);
        gotoMainActivity.putExtra(Constants.EXTRAS.WCA_ID, follower.wca_id());
        gotoMainActivity.putExtra(Constants.EXTRAS.USERNAME, follower.name());

        PendingIntent gotoMainActivityAction = PendingIntent.getActivity(this, 0, gotoMainActivity, PendingIntent.FLAG_CANCEL_CURRENT);

        // Partager
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, bigContent.toString());

        Intent shareChooser = Intent.createChooser(share, getString(R.string.share));
        PendingIntent shareAction = PendingIntent.getActivity(this, 0, shareChooser, PendingIntent.FLAG_CANCEL_CURRENT);

        // Parametres
        Intent gotoSettings = new Intent(this, SettingsActivity.class);
        PendingIntent gotoSettingsAction = PendingIntent.getActivity(this, 0, gotoSettings, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setTicker(content)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(gotoMainActivityAction)
                .addAction(R.drawable.ic_share, getString(R.string.share), shareAction)
                .addAction(R.drawable.ic_settings, getString(R.string.settings), gotoSettingsAction)
                .setStyle(bigContent);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(notif_id, notification.build());

        notif_id++;
    }

    @SuppressWarnings("ConstantConditions")
    private boolean singleChanged(Record oldRecord, com.adrastel.niviel.models.readable.Record newRecord) {
        return oldRecord.single() != null && newRecord.getSingle() != null && !oldRecord.single().equals(newRecord.getSingle());
    }


    @SuppressWarnings("ConstantConditions")
    private boolean averageChanged(Record oldRecord, com.adrastel.niviel.models.readable.Record newRecord) {
        return oldRecord.average() != null && newRecord.getAverage() != null && !oldRecord.average().equals(newRecord.getAverage());
    }



    private void callData(String wca_id, final dataCallback callback) {

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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(!response.isSuccessful()) {
                    return;
                }

                Document document = Jsoup.parse(response.body().string());
                response.close();

                ArrayList<com.adrastel.niviel.models.readable.Record> records = RecordProvider.getRecord(getApplicationContext(), document);

                callback.onSuccess(records);
            }
        });

    }

    private interface dataCallback {
        void onSuccess(ArrayList<com.adrastel.niviel.models.readable.Record> records);
    }
}
