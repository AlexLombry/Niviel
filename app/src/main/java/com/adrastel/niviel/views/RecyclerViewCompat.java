package com.adrastel.niviel.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.adrastel.niviel.assets.Log;

public class RecyclerViewCompat extends RecyclerView {

    private View emptyView;

    private boolean isLoading = true;

    public RecyclerViewCompat(Context context) {
        super(context);
    }

    public RecyclerViewCompat(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewCompat(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void checkIfEmpty() {
        if(emptyView != null && getAdapter() != null && !isLoading) {

            if(getAdapter().getItemCount() == 0) {
                RecyclerViewCompat.this.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                Log.i("RecyclerView empty");
            }
            else {
                RecyclerViewCompat.this.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                Log.i("RecyclerView not empty");
            }
        }
    }

    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
        }
    };

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdaper = getAdapter();

        if(oldAdaper != null) {
            oldAdaper.unregisterAdapterDataObserver(observer);
        }

        super.setAdapter(adapter);

        if(adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    public void notifyLoadingStop() {
        isLoading = false;
    }
}
