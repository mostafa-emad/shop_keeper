package com.shoppament.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppament.R;
import com.shoppament.databinding.ItemSlotTimeBinding;

import java.util.List;

public class ItemsRecyclerAdapter extends BaseRecyclerAdapter {
    private List<String> items;

    public ItemsRecyclerAdapter(List<String> items, Activity activity) {
        this.items = items;
        this.activity=activity;
    }

    public void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSlotTimeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_slot_time,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final MyViewHolder holder = (MyViewHolder) viewHolder;
        final String item = items.get(position);
        try{
            holder.binding.slotTimeTxt.setText(item);
            holder.binding.removeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,1);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemSlotTimeBinding binding;

        MyViewHolder(ItemSlotTimeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
