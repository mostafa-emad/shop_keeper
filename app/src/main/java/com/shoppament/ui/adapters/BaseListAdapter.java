package com.shoppament.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.shoppament.utils.callbacks.OnObjectChangedListener;

public class BaseListAdapter extends BaseAdapter {
    protected LayoutInflater layoutInflater;
    protected Activity activity;
    protected OnObjectChangedListener onObjectChangedListener;

    public void setOnObjectChangedListener(OnObjectChangedListener onObjectChangedListener) {
        this.onObjectChangedListener = onObjectChangedListener;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public int pxFromDp(float dp) {
        return (int) (dp * activity.getResources().getDisplayMetrics().density);
    }
}
