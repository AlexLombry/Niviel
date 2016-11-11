package com.adrastel.niviel.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecyclerViewTest extends RecyclerView {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private View emptyView;
    private RecyclerViewTest recyclerView = this;

    private OkHttpClient client = new OkHttpClient();

    public RecyclerViewTest(Context context) {
        super(context);
    }

    public RecyclerViewTest(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewTest(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initRecyclerViewCompat(SwipeRefreshLayout swipeRefreshLayout, ProgressBar progressBar, View emptyView) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.progressBar = progressBar;
        this.emptyView = emptyView;
    }

    public void hideAll() {
        swipeRefreshLayout.setRefreshing(false);
        emptyView.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        recyclerView.setVisibility(GONE);
    }

    public void showRecycler() {
        hideAll();
        recyclerView.setVisibility(VISIBLE);
    }

    public void showProgress() {
        if(getAdapter() != null && getAdapter().getItemCount() != 0) {
            showRecycler();
        }

        else if(!swipeRefreshLayout.isRefreshing()){
            hideAll();
            progressBar.setVisibility(VISIBLE);
        }
    }

    public void showEmpty() {
        if(getAdapter() != null && getAdapter().getItemCount() != 0) {
            showRecycler();
        }

        else {
            hideAll();
            emptyView.setVisibility(VISIBLE);
        }
    }

    public void callData(HttpUrl url, final Callback callback) {
        showProgress();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        showEmpty();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showEmpty();
                        }
                    });
                }

                else {
                    callback.onSuccess(response.body().string());
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showRecycler();
                        }
                    });
                }

                response.close();
            }
        });
    }

    public interface Callback {
        void onSuccess(String response);
    }


}
