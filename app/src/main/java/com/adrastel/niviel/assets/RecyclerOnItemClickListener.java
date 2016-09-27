package com.adrastel.niviel.assets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerOnItemClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener itemClickListener;
    private GestureDetector gestureDetector;

    public RecyclerOnItemClickListener(Context context, OnItemClickListener onClickListener) {
        this.itemClickListener = onClickListener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {

        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

        if (view != null && itemClickListener != null && gestureDetector.onTouchEvent(e)) {

            itemClickListener.onClick(view, recyclerView.getChildAdapterPosition(view));

        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }


}
