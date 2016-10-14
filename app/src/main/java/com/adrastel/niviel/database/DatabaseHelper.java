package com.adrastel.niviel.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.adrastel.niviel.FollowerModel;
import com.adrastel.niviel.RecordModel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static AtomicInteger openCount = new AtomicInteger();
    private static SQLiteDatabase database;

    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 9;

    private static DatabaseHelper instance;

    //<editor-fold desc="Database init">
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHelper(context);
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if(openCount.incrementAndGet() == 1) {
            database = getWritableDatabase();
        }

        return database;
    }

    public synchronized void closeDatabase() {
        if(openCount.decrementAndGet() == 0) {
            database.close();
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Follower.CREATE_TABLE);
        db.execSQL(Record.CREATE_TABLE);
        db.execSQL(History.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



        db.execSQL(Follower.DELETE_TABLE);
        db.execSQL(Record.DELETE_TABLE);
        db.execSQL(History.DELETE_TABLE);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    //</editor-fold>

    //<editor-fold desc="Followers">
    public long insertFollower(String name, String wca_id) {

        try {
            SQLiteDatabase db = openDatabase();

            return db.insert(Follower.TABLE_NAME, null, Follower.FACTORY.marshal()
                    .name(name)
                    .wca_id(wca_id)
                    .asContentValues());

        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

        return -1;
    }

    public void deleteFollower(long follower_id) {

        try {
            SQLiteDatabase db = openDatabase();

            db.execSQL(Follower.DELETE_FOLLOWER, new String[] {String.valueOf(follower_id)});
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

    }

    public Follower selectFollowerFromId(long id) {

        try {
            SQLiteDatabase db = openDatabase();

            Cursor cursor = db.rawQuery(Follower.SELECT_FOLLOWER_FROM_ID, new String[] {String.valueOf(id)});
            cursor.moveToFirst();

            Follower follower = Follower.SELECT_FOLLOWER_FROM_ID_MAPPER.map(cursor);

            cursor.close();

            return follower;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

        return null;
    }

    public ArrayList<Follower> selectAllFollowers() {

        ArrayList<Follower> followers = new ArrayList<>();

        try {
            Cursor cursor = openDatabase().rawQuery(FollowerModel.SELECT_ALL, null);

            while (cursor.moveToNext()) {

                followers.add(Follower.SELECT_ALL_MAPPER.map(cursor));
            }

            cursor.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

        return followers;

    }

    public ArrayList<Record> selectRecordsFromFollower(long follower_id) {

        ArrayList<Record> records = new ArrayList<>();

        try {
            Cursor cursor = openDatabase().rawQuery(RecordModel.SELECT_FROM_FOLLOWER, new String[] {String.valueOf(follower_id)});

            while (cursor.moveToNext()) {
                records.add(Record.SELECT_FROM_FOLLOWER_MAPPER.map(cursor));
            }

            cursor.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

        return records;

    }

    public long getFollowerIdFromWca(String wca_id) {

        try {
            SQLiteDatabase db = openDatabase();

            Cursor cursor = db.rawQuery(Follower.SELECT_ID_FROM_WCA, new String[] {wca_id});
            cursor.moveToFirst();

            long id = Follower.SELECT_ID_FROM_WCA_MAPPER.map(cursor);
            cursor.close();

            return id;
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

        return -1;
    }
    //</editor-fold>

    //<editor-fold desc="Records">
    public long insertRecord(long follower_id, @NonNull String event, String single, long nr_single, long cr_single, long wr_single, String average, long nr_average, long cr_average, long wr_average) {

        try {
            SQLiteDatabase db = openDatabase();

            return db.insert(Record.TABLE_NAME, null, Record.FACTORY.marshal()
                .follower(follower_id)
                .event(event)
                .single(single)
                .nr_single(nr_single)
                .cr_single(cr_single)
                .wr_single(wr_single)
                .average(average)
                .nr_average(nr_average)
                .cr_average(cr_average)
                .wr_average(wr_average).asContentValues());


        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

        return -1;
    }

    public void updateRecord(long follower_id, String event, ContentValues contentValues) {

        try {
            SQLiteDatabase db = openDatabase();

            db.update(
                Record.TABLE_NAME, contentValues,
                Record.FOLLOWER + "= ? AND " + Record.EVENT + "= ?",
                new String[]{String.valueOf(follower_id), event});
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }
    }

    public void deleteRecords(long follower_id) {
        try {
            SQLiteDatabase db = openDatabase();
            db.execSQL(Record.DELETE_RECORDS, new Long[] {follower_id});
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

    }
    //</editor-fold>

    //<editor-fold desc="History">
    public void insertHistory(long follower_id, String event, String competition, String round, String place, String best, String average, String result_details) {
        try {
            SQLiteDatabase db = openDatabase();

            db.insert(History.TABLE_NAME, null, History.FACTORY.marshal()
                    .follower(follower_id)
                    .event(event)
                    .competition(competition)
                    .round(round)
                    .place(place)
                    .best(best)
                    .average(average)
                    .result_details(result_details)
                    .asContentValues()
            );
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }
    }

    public void deleteHistories(long follower_id) {
        try {
            SQLiteDatabase db = openDatabase();

            db.execSQL(History.DELETE_HISTORIES, new Long[] {follower_id});
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }
    }

    public ArrayList<History> selectHistoriesFromFollower(long follower_id) {

        ArrayList<History> histories = new ArrayList<>();

        try {
            SQLiteDatabase db = openDatabase();

            Cursor cursor = db.rawQuery(History.SELECT_FROM_FOLLOWER, new String[] {String.valueOf(follower_id)});

            while (cursor.moveToNext()) {

                histories.add(History.SELECT_ALL_MAPPER.map(cursor));

            }

            cursor.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

        return histories;

    }
    //</editor-fold>



}
