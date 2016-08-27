package com.adrastel.niviel.providers.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SearchProvider extends ContentProvider {

    public static final String AUTHORITY = "com.adrastel.niviel.providers.content.SearchProvider";
    private static final int USERS = 1;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        matcher.addURI(AUTHORITY, "users/#", USERS);

    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1, String s1) {

        switch (matcher.match(uri)) {

            case USERS:

                break;


        }

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
