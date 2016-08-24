package com.adrastel.niviel.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adrastel.niviel.FollowerModel;
import com.adrastel.niviel.assets.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static AtomicInteger openCount = new AtomicInteger();
    private static SQLiteDatabase database;

    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 1;

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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(Follower.DELETE_TABLE);

        onCreate(db);
    }

    public void insertFollower(String name, String wca_id, long created_at) {

        try {
            SQLiteDatabase db = openDatabase();

            db.insert(Follower.TABLE_NAME, null, Follower.FACTORY.marshal()
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

}
