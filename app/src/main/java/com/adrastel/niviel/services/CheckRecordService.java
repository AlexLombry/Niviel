package com.adrastel.niviel.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.NotificationCompat;

import com.adrastel.niviel.R;
import com.adrastel.niviel.RecordModel;
import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.activities.SettingsActivity;
import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.assets.Cubes;
import com.adrastel.niviel.assets.InboxStyle;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.database.Record;
import com.adrastel.niviel.fragments.ProfileFragment;
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

public class CheckRecordService extends IntentService {

    DatabaseHelper database;

    public static final int SINGLE = 0;
    public static final int AVERAGE = 1;
    public static final int COUNTRY = 2;
    public static final int CONTINENT = 3;
    public static final int WORLD = 4;

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

    // todo: gerer le cas ou 2 battements de records
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
        Log.d(String.valueOf(oldRecords.size()), String.valueOf(newRecords.size()));


        if(oldRecords.size() == newRecords.size()) {

            for(int i = 0; i < oldRecords.size(); i++) {

                Record oldRecord = oldRecords.get(i);
                com.adrastel.niviel.models.readable.Record newRecord = newRecords.get(i);

                // Si ils ont le meme event, on peut comparer
                if(oldRecord.event().equals(newRecord.getEvent())) {

                    try {
                        /*checkRank(newRecord, follower, SINGLE, COUNTRY, oldRecord.nr_single(), Long.parseLong(newRecord.getNr_single()));
                        checkRank(newRecord, follower, SINGLE, CONTINENT, oldRecord.cr_single(), Long.parseLong(newRecord.getCr_single()));
                        checkRank(newRecord, follower, SINGLE, WORLD, oldRecord.wr_single(), Long.parseLong(newRecord.getWr_single()));
                        checkRank(newRecord, follower, AVERAGE, COUNTRY, oldRecord.nr_average(), Long.parseLong(newRecord.getNr_average()));
                        checkRank(newRecord, follower, AVERAGE, CONTINENT, oldRecord.cr_average(), Long.parseLong(newRecord.getCr_average()));
                        checkRank(newRecord, follower, AVERAGE, WORLD, oldRecord.wr_average(), Long.parseLong(newRecord.getWr_average()));*/

                        //checkRecord(newRecord, follower, SINGLE, , Assets.dateToMillis(newRecord.getSingle()))

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
                            //database.updateRecord(follower._id(), newRecord.getEvent(), values.asContentValues());
                        }
                    }

                    catch (Exception e) {
                        Log.e("Invalid long");
                    }
                }

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


    private boolean checkRank(com.adrastel.niviel.models.readable.Record record, Follower follower, int type, int region, long oldRank, long newRank) {

        Log.v(type + " " + region, oldRank + "->" + newRank);

        if (oldRank > newRank && oldRank != 0 && newRank != 0) {
            notifyChangeRank(record, follower, type, region, oldRank, newRank);
            return true;
        }

        return false;

    }
//<editor-fold desc="notifyCheckRank">
    private void notifyChangeRank(com.adrastel.niviel.models.readable.Record record, Follower follower, int type, int region, long oldRank, long newRank){
        // content values to update the record
        RecordModel.Marshal marshal = Record.FACTORY.marshal();
        // Fait la phrase de notification
        String type_format = null;
        String region_format = null;
        String time = null;

        switch (type) {
            case SINGLE:
                type_format = getString(R.string.notif_single);
                marshal.single(record.getSingle());
                marshal.nr_single(Long.parseLong(record.getNr_single()));
                marshal.cr_single(Long.parseLong(record.getCr_single()));
                marshal.wr_single(Long.parseLong(record.getWr_single()));
                break;

            case AVERAGE:
                type_format = getString(R.string.notif_average);
                time = record.getAverage();
                marshal.average(record.getAverage());
                marshal.nr_average(Long.parseLong(record.getNr_average()));
                marshal.cr_average(Long.parseLong(record.getCr_average()));
                marshal.wr_average(Long.parseLong(record.getWr_average()));
                break;
        }

        switch (region) {
            case COUNTRY:
                region_format = getString(R.string.notif_country);
                break;

            case CONTINENT:
                region_format = getString(R.string.notif_continent);
                break;

            case WORLD:
                region_format = getString(R.string.notif_world);
                break;
        }

        // notification
        String notifMessage = String.format(getString(R.string.notif_new_rank_message), oldRank, newRank, region_format, type_format, record.getEvent(), time);
        String shareMessage = String.format(getString(R.string.share_new_rank), follower.name(), oldRank, newRank, region_format, type_format, record.getEvent(), time);

        String msg = notifMsgs.get(follower._id()) != null ? notifMsgs.get(follower._id()) : "";

        notifMsgs.put(follower._id(), (msg + "\n" + notifMessage).trim());

        Intent gotoMainActivity = new Intent(this, MainActivity.class);
        gotoMainActivity.putExtra(Constants.EXTRAS.FRAGMENT, ProfileFragment.FRAGMENT_TAG);
        gotoMainActivity.putExtra(Constants.EXTRAS.WCA_ID, follower.wca_id());
        gotoMainActivity.putExtra(Constants.EXTRAS.USERNAME, follower.name());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, gotoMainActivity, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, shareMessage);

        Intent chooseAndShare = Intent.createChooser(share, getString(R.string.share));

        PendingIntent mainAction = PendingIntent.getActivity(this, 0, chooseAndShare, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), Cubes.getImage((int) follower._id())))
                .setContentTitle(follower.name())
                .setContentText(notifMessage)
                .setTicker(notifMessage)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_followers, "Partager", mainAction)
                .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(notifMsgs.get(follower._id())));

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify((int) follower._id(), notification.build());

        database.updateRecord(follower._id(), record.getEvent(), marshal.asContentValues());
    }
    //</editor-fold>



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

                try {
                    Log.d("nr", records.get(0).getNr_average());
                    records.get(0).setSingle("3.60");
                    records.get(0).setAverage("3.70");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                callback.onSuccess(records);
            }
        });

    }

    private interface dataCallback {
        void onSuccess(ArrayList<com.adrastel.niviel.models.readable.Record> records);
    }
}
