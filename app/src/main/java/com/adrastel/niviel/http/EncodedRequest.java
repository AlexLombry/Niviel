package com.adrastel.niviel.http;

import android.app.Activity;
import android.widget.Toast;

import com.adrastel.niviel.assets.Assets;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

/**
 * La classe est une StringRequest mais elle encode la reponse en UTF-8
 */
public class EncodedRequest extends StringRequest {

    public Activity activity;

    public EncodedRequest(Activity activity, int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);

        this.activity = activity;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        try {

            String encodedString = new String(response.data, "UTF-8");

            return Response.success(encodedString, HttpHeaderParser.parseCacheHeaders(response));


        } catch (UnsupportedEncodingException e) {

            final VolleyError volleyError = new ParseError(e);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(volleyError.networkResponse != null) {

                        String message = activity.getString(Assets.onHttpError(volleyError.networkResponse.statusCode));

                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                    }
                }
            });
            return Response.error(volleyError);

        }
    }
}
