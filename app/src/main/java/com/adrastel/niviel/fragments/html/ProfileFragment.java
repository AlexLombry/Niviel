package com.adrastel.niviel.fragments.html;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.fragments.BaseFragment;
import com.adrastel.niviel.models.readable.Profile;
import com.adrastel.niviel.volley.EncodedRequest;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends BaseFragment {

    private RequestQueue requestQueue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = Volley.newRequestQueue(getContext());

        requestData(new RequestDataCallback() {
            @Override
            public void onSuccess(Profile profile) {

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

        EncodedRequest request = new EncodedRequest(getActivity(), Request.Method.GET, "https://www.worldcubeassociation.org/api/v0/users/2016DERO01", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject root = new JSONObject(response);

                    // On serialise l'objet
                    JSONObject user = root.getJSONObject("user");

                    Gson gson = new Gson();

                    Profile profile = gson.fromJson(user.toString(), Profile.class);
                    Log.d(profile.getUrl());

                    callback.onSuccess(profile);
                    callback.postRequest();
                } catch (JSONException e) {
                    Toast.makeText(getContext(), getString(R.string.error_loading), Toast.LENGTH_LONG).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError(error);
                callback.postRequest();
            }
        });

        requestQueue.add(request);

    }

    private interface RequestDataCallback {
        void onSuccess(Profile profile);
        void onError(VolleyError error);
        void postRequest();
    }
}
