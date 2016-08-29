package com.adrastel.niviel.fragments.html;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.http.HttpCallback;
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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ProfileFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }

    @Override
    public int getTitle() {
        return R.string.profile;
    }

    @Override
    public int getPrimaryColor() {
        return R.color.orange;
    }

    @Override
    public int getPrimaryDarkColor() {
        return R.color.orangeDark;
    }

    @Override
    public int getFabVisibility() {
        return View.VISIBLE;
    }

    @Override
    public int getFabIcon() {
        return R.drawable.ic_followers;
    }

    @Override
    public void onFabClick(View view) {

    }

    private void requestData() {

        OkHttpClient client = new OkHttpClient();

        // https://www.worldcubeassociation.org/api/v0/search/users?q=mathias%20deroubaix

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addEncodedPathSegments("api/v0/search/users")
                .addEncodedQueryParameter("q", "Mathias Deroubaix")
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);


        call.enqueue(new HttpCallback(getActivity()) {
            @Override
            public void onResponse(String response){


                JsonParser jsonParser = new JsonParser();
                JsonElement jsonTree = jsonParser.parse(response);

                JsonObject jsonObject = jsonTree.getAsJsonObject();

                JsonArray result = jsonObject.getAsJsonArray("result");

                Gson gson = new Gson();
                ArrayList<User> users = gson.fromJson(result, new TypeToken<ArrayList<User>>() {
                }.getType());

                for (User user : users) {
                    Log.d(user.getWca_id());

                }
            }

            @Override
            public void onFailure() {

            }

        });
    }
}
