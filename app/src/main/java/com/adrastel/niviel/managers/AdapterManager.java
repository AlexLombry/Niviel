package com.adrastel.niviel.managers;

import android.app.Activity;

import com.adrastel.niviel.adapters.BaseAdapter;
import com.adrastel.niviel.models.BaseModel;

import java.util.ArrayList;

public class AdapterManager<M extends BaseModel, A extends BaseAdapter> {

    private Activity activity;
    private ArrayList<M> datas = new ArrayList<>();
    private A adapter;

    public AdapterManager(A adapter, Activity activity) {
        this.adapter = adapter;
        this.activity = activity;
    }

    public void refreshDatas(ArrayList<M> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        adapter.notifyDataSetChanged();
    }

}
