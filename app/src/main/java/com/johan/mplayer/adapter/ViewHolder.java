package com.johan.mplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by johan on 2018/11/7.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;

    public ViewHolder(View itemView) {
        super(itemView);
        views = new SparseArray<>();
    }

    public <T extends View> T getView(int id) {
        View view = views.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            views.put(id, view);
        }
        return (T) view;
    }

    public static ViewHolder create(ViewGroup parent, int layout) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(itemView);
    }

}
