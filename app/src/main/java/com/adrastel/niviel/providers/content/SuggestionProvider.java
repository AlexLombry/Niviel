package com.adrastel.niviel.providers.content;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.models.readable.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SuggestionProvider extends ContentProvider {
    public SuggestionProvider() {
    }

    private OkHttpClient client = new OkHttpClient();

    private ArrayList<User> users = new ArrayList<>();
    private SuggestionProvider _this = this;

    private static final String[] COLUMNS = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
            SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2
    };

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {


        String query = uri
                .getLastPathSegment()
                .trim()
                .replace(" ", "+");


        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addEncodedPathSegments("api/v0/search")
                .addEncodedQueryParameter("q", query)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String data = response.body().string();
                    response.close();

                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonTree = jsonParser.parse(data);

                    JsonObject jsonObject = jsonTree.getAsJsonObject();

                    JsonArray result = jsonObject.getAsJsonArray("result");

                    Gson gson = new Gson();
                    ArrayList<User> users = gson.fromJson(result, new TypeToken<ArrayList<User>>() {
                    }.getType());

                    _this.users.clear();
                    _this.users.addAll(users);

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Cree le curseur

        MatrixCursor cursor = new MatrixCursor(COLUMNS);


            int limit = 10;

            for (int i = 0; i < users.size() && cursor.getCount() < limit; i++) {

                User user = users.get(i);

                // verifie que l'utilisateur a un id wca
                if ((user.getType().equals("person") || user.getType().equals("user")) && user.getWca_id() != null) {

                    cursor.addRow(new Object[]{i, user.getWca_id(), user.getName(), user.getName(), user.getWca_id()});
                    i++;
                }
            }

        return cursor;
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
