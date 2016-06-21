package com.adrastel.niviel.connectivity;

import android.content.Context;
import android.util.Log;

import com.adrastel.niviel.WCA.Profile;
import com.adrastel.niviel.callbacks.ProfileCallback;
import com.adrastel.niviel.callbacks.VolleyCallback;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class WcaAPI {

    private Context context;
    private String access_token;
    private RequestQueue requestQueue;
    private int method = Request.Method.GET;

    public WcaAPI(Context context, String access_token) {
        this.context = context;
        this.access_token = access_token;
    }

    public void getUser(String wca_id, final ProfileCallback callback) {
        String url = "https://www.worldcubeassociation.org/api/v0/users/" + wca_id;

        request(url, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {

                try {
                    Profile profile = Profile.deserialize(response);
                    callback.onSuccess(profile);

                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError(-1, e.getMessage());
                }
            }

            @Override
            public void onError(int errorCode, String body) {
                callback.onError(errorCode, body);
            }

        });
    }

    private void request(String url, final VolleyCallback callback) {
        request(url, false, callback);
    }

    private void request(String url, final boolean needAuth, final VolleyCallback callback) {
        this.requestQueue = Volley.newRequestQueue(context);
        Log.d("niviel", "nouvelle requete");

        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    int errorCode = error.networkResponse.statusCode;
                    String body = new String(error.networkResponse.data, "utf-8");
                    callback.onError(errorCode, body);

                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();

                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();

                if(needAuth) {
                    params.put("Authorization", "Bearer " + access_token);
                }

                return params;
            }
        };

        requestQueue.add(request);
    }

}
