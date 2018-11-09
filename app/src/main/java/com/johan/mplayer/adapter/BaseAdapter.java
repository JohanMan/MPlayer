package com.johan.mplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by johan on 2018/11/7.
 */

public abstract class BaseAdapter <T> extends RecyclerView.Adapter <ViewHolder> implements View.OnClickListener {

    protected List<T> dataList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public BaseAdapter(List<T> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = getLayout(viewType);
        return ViewHolder.create(parent, layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        bindView(holder, position, dataList.get(position), getItemViewType(position));
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(position);
        }
    }

    public abstract int getLayout(int viewType);

    public abstract void bindView(ViewHolder holder, int position, T data, int viewType);

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);
    }

}
