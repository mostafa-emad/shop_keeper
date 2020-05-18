package com.shoppament.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.shoppament.utils.callbacks.OnObjectChangedListener;


public class BaseRecyclerAdapter extends RecyclerView.Adapter{
    protected LayoutInflater layoutInflater;
    protected Activity activity;
    protected OnObjectChangedListener onObjectChangedListener;

    public void setOnObjectChangedListener(OnObjectChangedListener onObjectChangedListener) {
        this.onObjectChangedListener = onObjectChangedListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public int pxFromDp(final float dp) {
        return (int) (dp * activity.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

}
