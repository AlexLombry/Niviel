package com.adrastel.niviel.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adrastel.niviel.Models.BaseModel;
import com.adrastel.niviel.Models.Record;
import com.adrastel.niviel.R;
import com.adrastel.niviel.WCA.RecordProvider;
import com.adrastel.niviel.adapters.RecordAdapter;
import com.adrastel.niviel.assets.Constants;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class RankingFragment extends GenericFragment<Record, RecordAdapter> {

    private Activity activity;
    private RecordAdapter recordAdapter = new RecordAdapter(getDatas());


    @Override
    protected String getUrl() {
        return "https://www.worldcubeassociation.org/results/p.php?i=2016DERO01";
    }

    @Override
    protected RecordAdapter getAdapter() {
        return recordAdapter;
    }


    @Override
    protected String getStorage() {
        return Constants.STORAGE.RANKING;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_test, container, false);


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_list_test_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(getAdapter());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        requestData(activity, new requestDataCallback() {
            @Override
            public ArrayList<? extends BaseModel> parseDatas(Document document) {
                return RecordProvider.getRecord(activity, document, true);
            }

            @Override
            public void runOnUIThread(ArrayList<? extends BaseModel> datas) {

                // On sauvegarde et raffrechie la liste
                refreshAndSaveData((ArrayList<Record>) datas);
            }

        });
    }

    @Override
    public String getTitle() {
        return activity != null ? activity.getString(R.string.ranking) : null;
    }

}
