package com.shoppament.ui.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppament.R;

import java.util.ArrayList;

public class PopupOptionsListAdapter extends BaseListAdapter {
    private ArrayList<String> options;
    private int layout = R.layout.item_popup_option;

    public PopupOptionsListAdapter(Activity activity, ArrayList<String> options) {
        this.options = options;
        this.layoutInflater = activity.getLayoutInflater();
        this.activity = activity;
    }

    public PopupOptionsListAdapter(Activity activity, ArrayList<String> options, int layout) {
        this.options = options;
        this.layoutInflater = activity.getLayoutInflater();
        this.activity = activity;
        this.layout=layout;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    public String getItem(int index) { // TODO Auto-generated method
        return options.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(layout, parent, false);
            holder = new ViewHolder();
            assert view != null;
            holder.nameTxt = view.findViewById(R.id.option_name_txt);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String item = options.get(position);
        holder.nameTxt.setText(item);


        return view;
    }

    private class ViewHolder {
        TextView nameTxt;

    }

}
