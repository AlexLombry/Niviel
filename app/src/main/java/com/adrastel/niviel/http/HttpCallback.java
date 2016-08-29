package com.adrastel.niviel.http;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.adrastel.niviel.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class HttpCallback implements okhttp3.Callback {

    private Activity activity;

    public HttpCallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {

        try {
            if (!response.isSuccessful()) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        @StringRes int error = R.string.error_connection;
                        // Si il s'agit d'une erreur du serveur
                        if (String.valueOf(response.code()).charAt(0) == '5') {
                            error = R.string.err_server;
                        }

                        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                    }
                });
            }

            else {
                String data = response.body().string();
                response.close();

                onResponse(data);
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, R.string.error_connection, Toast.LENGTH_LONG).show();
            }
        });

        onFailure();
    }

    public abstract void onResponse(String response);
    public abstract void onFailure();
}
