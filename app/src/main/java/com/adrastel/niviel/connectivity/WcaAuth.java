package com.adrastel.niviel.connectivity;

import android.content.Context;
import android.util.Log;

import com.adrastel.niviel.assets.Constants;
import com.adrastel.niviel.callbacks.VolleyCallback;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class WcaAuth {

    private Context context;

    public WcaAuth(Context context) {
        this.context = context;
    }

    /**
     * Récupère un access_token à l'aide du token en faisant une requete HTTP de type POST, le résultat en renvoyé en callback
     *
     * @param token Code d'accès
     * @param callback On retourne les informations par le callback
     */
    public void getAccessToken(final String token, final VolleyCallback callback) {

        String url = Constants.API.ACCESS_TOKEN;

        RequestQueue queue = Volley.newRequestQueue(context);


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                callback.onSuccess(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("niviel", "HTTP error");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("grant_type", Constants.API.GRANT_TYPE);
                params.put("client_id", Constants.SECRETS.CLIENT_ID);
                params.put("client_secret", Constants.SECRETS.CLIENT_SECRET);
                params.put("redirect_uri", Constants.API.REDIRECT_URI);
                params.put("code", token);


                return params;
            }

        };

        queue.add(request);
    }



}
