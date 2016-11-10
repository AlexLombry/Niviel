package com.adrastel.niviel.managers;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ProgressBar;

import com.adrastel.niviel.http.HttpCallback;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpManager {

    protected Activity activity;

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ProgressBar progressBar;

    protected OkHttpClient client = new OkHttpClient();
    protected Call call;

    public HttpManager(Activity activity) {
        this.activity = activity;
    }

    public HttpManager(Activity activity, SwipeRefreshLayout swipeRefreshLayout, ProgressBar progressBar) {
        this.activity = activity;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.progressBar = progressBar;
    }

    public void callData(HttpUrl url, final SuccessCallback callback) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        call = client.newCall(request);


        call.enqueue(new HttpCallback(activity) {
            @Override
            public void onResponse(String response) {
                stopLoaders();
                callback.onSuccess(response);
            }

            @Override
            public void onFailure() {
                stopLoaders();
            }
        });

    }


    public interface SuccessCallback {
        void onSuccess(String response);
    }

    public interface FailureCallback {
        void onError();
    }

    public void stopLoaders() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if(progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }


}
