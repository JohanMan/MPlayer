package com.johan.mplayer.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by johan on 2018/11/8.
 */

public class EmptyItemDecoration extends RecyclerView.ItemDecoration {

    private int dividerHeight;

    public EmptyItemDecoration(int dividerHeight) {
        this.dividerHeight = dividerHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, dividerHeight);
    }

}
