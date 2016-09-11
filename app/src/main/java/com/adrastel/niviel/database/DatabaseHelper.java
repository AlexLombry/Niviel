package com.adrastel.niviel.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.adrastel.niviel.FollowerModel;
import com.adrastel.niviel.assets.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static AtomicInteger openCount = new AtomicInteger();
    private static SQLiteDatabase database;

    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 3;

    private static DatabaseHelper instance;

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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(Follower.DELETE_TABLE);
        db.execSQL(Record.DELETE_TABLE);

        onCreate(db);
    }


    //<editor-fold desc="Followers">
    public long insertFollower(String name, String wca_id, long created_at) {

        try {
            SQLiteDatabase db = openDatabase();

            return db.insert(Follower.TABLE_NAME, null, Follower.FACTORY.marshal()
                    .name(name)
                    .wca_id(wca_id)
                    .created_at(created_at)
                    .asContentValues());
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

        return 0;
    }

    public void deleteFollower(String wca_id) {

        try {
            SQLiteDatabase db = openDatabase();

            db.execSQL(Follower.DELETE_FOLLOWER, new String[] {wca_id});
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            closeDatabase();
        }

    }

    public ArrayList<Follower> selectAllFollowers() {

        ArrayList<Follower> followers = new ArrayList<>();

        try {
            Cursor cursor = openDatabase().rawQuery(FollowerModel.SELECT_ALL, null);
            cursor.moveToFirst();

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

        return 0;
    }
    //</editor-fold>

    public void insertRecord(long follower_id, @NonNull String event, long single, long nr_single, long cr_single, long wr_single, long average, long nr_average, long cr_average, long wr_average) {

        try {
            SQLiteDatabase db = openDatabase();

            db.insert(Record.TABLE_NAME, null, Record.FACTORY.marshal()
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

}
