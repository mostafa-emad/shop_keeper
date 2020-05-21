package com.shoppament.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppament.R;
import com.shoppament.databinding.ItemPopupOptionBinding;
import com.shoppament.utils.callbacks.OnOptionsListener;

import java.util.List;

public class OptionsRecyclerAdapter extends BaseRecyclerAdapter{
    private List<String> options;
    private OnOptionsListener optionsListener;

    public OptionsRecyclerAdapter(List<String> options, Activity activity) {
        this.options = options;
        this.activity = activity;
    }

    public void setOptionsListener(OnOptionsListener optionsListener) {
        this.optionsListener = optionsListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPopupOptionBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_popup_option,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final MyViewHolder holder = (MyViewHolder) viewHolder;
        final String option = options.get(position);
        try{
            holder.binding.optionNameTxt.setText(option);
            holder.binding.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(optionsListener!=null){
                        optionsListener.onSelected(option,position);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemPopupOptionBinding binding;

        MyViewHolder(ItemPopupOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
