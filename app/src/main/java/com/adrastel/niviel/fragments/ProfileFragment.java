package com.adrastel.niviel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.HistoryAdapter;
import com.adrastel.niviel.adapters.RecordAdapter;
import com.adrastel.niviel.http.HttpCallback;
import com.adrastel.niviel.models.readable.History;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.providers.html.HistoryProvider;
import com.adrastel.niviel.providers.html.RecordProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class ProfileFragment extends BaseFragment {

    Unbinder unbinder;

    @BindView(R.id.recycler_records) RecyclerView recyclerRecords;
    @BindView(R.id.recycler_history) RecyclerView recyclerHistory;

    RecordAdapter recordAdapter;
    HistoryAdapter historyAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recordAdapter = new RecordAdapter(getActivity());
        historyAdapter = new HistoryAdapter(getActivity());

        requestData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        recyclerRecords.setHasFixedSize(true);
        recyclerRecords.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerRecords.setAdapter(recordAdapter);

        recyclerHistory.setHasFixedSize(true);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerHistory.setAdapter(historyAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_Profile;
    }

    private void requestData() {

        OkHttpClient client = new OkHttpClient();

        // https://www.worldcubeassociation.org/api/v0/search/users?q=mathias%20deroubaix

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.worldcubeassociation.org")
                .addEncodedPathSegments("results/p.php")
                .addEncodedQueryParameter("i", "2016DERO01")
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);


        call.enqueue(new HttpCallback(getActivity()) {
            @Override
            public void onResponse(String response){

                Document document = Jsoup.parse(response);

                recordAdapter.refreshData(RecordProvider.getRecord(getActivity(), document, false));
                historyAdapter.refreshData(HistoryProvider.getHistory(getActivity(), document));

            }

            @Override
            public void onFailure() {

            }

        });
    }
}
