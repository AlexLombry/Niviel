package com.adrastel.niviel.HTTP;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

/**
 * La classe est une StringRequest mais elle encode la reponse en UTF-8
 */
public class EncodedRequest extends StringRequest {

    public EncodedRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        try {

            String encodedString = new String(response.data, "UTF-8");

            return Response.success(encodedString, HttpHeaderParser.parseCacheHeaders(response));


        } catch (UnsupportedEncodingException e) {

            return Response.error(new ParseError(e));

        }
    }
}
