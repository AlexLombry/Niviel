package com.adrastel.niviel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.adrastel.niviel.R;
import com.google.android.gms.ads.NativeExpressAdView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.adview) NativeExpressAdView adView;

    public AdHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
