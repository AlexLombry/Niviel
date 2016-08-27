package com.adrastel.niviel.fragments.html;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.models.readable.User;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ProfileFragment extends BaseFragment {

    private RequestQueue requestQueue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(getContext());

        requestData(new RequestDataCallback() {
            @Override
            public void onSuccess(User user) {

            }

            @Override
            public void onError(VolleyError error) {

            }

            @Override
            public void postRequest() {

            }
        });
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

    private void requestData(final RequestDataCallback callback) {

        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://www.worldcubeassociation.org/api/v0/search/users?q=Mathias%20Deroubaix")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        if (!response.isSuccessful()) {
                            Log.e("Error");
                        } else {

                            String data = response.body().string();

                            JSONObject jsonTree = new JSONObject(data);


                            JSONArray results = jsonTree.getJSONArray("result");
                            Log.d(results.toString());
                            Gson gson = new Gson();
                            ArrayList<User> users = gson.fromJson(results.toString(), new TypeToken<ArrayList<User>>() {
                            }.getType());

                            for (User user : users) {
                                Log.d(user.getWca_id());
                            }



                        }

                    }

                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    private interface RequestDataCallback {
        void onSuccess(User user);
        void onError(VolleyError error);
        void postRequest();
    }
}
