package com.adrastel.niviel.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class RecyclerViewCompat extends RecyclerView {

    private View emptyView;

    public RecyclerViewCompat(Context context) {
        super(context);
    }

    public RecyclerViewCompat(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewCompat(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void checkIfEmpty() {
        if(emptyView != null && getAdapter() != null) {
            final boolean emptyViewVisibility = getAdapter().getItemCount() == 0;

            /*emptyView.setVisibility(emptyViewVisibility ? VISIBLE : GONE);
            setVisibility(emptyViewVisibility ? GONE : VISIBLE);*/

            if(getAdapter().getItemCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                RecyclerViewCompat.this.setVisibility(View.GONE);
            }
            else {
                emptyView.setVisibility(View.GONE);
                RecyclerViewCompat.this.setVisibility(View.VISIBLE);
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
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}
