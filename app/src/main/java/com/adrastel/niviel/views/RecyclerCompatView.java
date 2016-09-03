package com.adrastel.niviel.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class RecyclerCompatView extends RecyclerView {

    private View emptyView;

    public RecyclerCompatView(Context context) {
        super(context);
    }

    public RecyclerCompatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerCompatView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            //super.onChanged();
            Adapter<?> adapter = getAdapter();

            if(adapter != null && emptyView != null) {

                if(adapter.getItemCount() == 0) {
                    emptyView.setVisibility(VISIBLE);
                    RecyclerCompatView.this.setVisibility(GONE);
                }

                else {
                    emptyView.setVisibility(GONE);
                    RecyclerCompatView.this.setVisibility(VISIBLE);
                }

            }

        }
    };



    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if(adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        observer.onChanged();

    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}
