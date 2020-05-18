package com.shoppament.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppament.R;
import com.shoppament.data.models.SlotTimingModel;
import com.shoppament.databinding.ItemSlotTimeBinding;

import java.util.List;

public class SlotsTimingRecyclerAdapter extends BaseRecyclerAdapter {
    private List<SlotTimingModel> slotTimingModels;

    public SlotsTimingRecyclerAdapter(List<SlotTimingModel> slotTimingModels, Activity activity) {
        this.slotTimingModels = slotTimingModels;
        this.activity=activity;
    }

    public void setSlotTimingModels(List<SlotTimingModel> slotTimingModels) {
        this.slotTimingModels = slotTimingModels;
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
        final SlotTimingModel slotTimingModel = slotTimingModels.get(position);
        try{
            StringBuilder slotTimeBuilder = new StringBuilder(activity.getResources().getString(R.string.title_slot));
            slotTimeBuilder.append(" ");
            slotTimeBuilder.append(position+1);
            slotTimeBuilder.append(" - ");
            slotTimeBuilder.append(slotTimingModel.getFromDate());
            slotTimeBuilder.append(" ");
            slotTimeBuilder.append(activity.getResources().getString(R.string.title_to));
            slotTimeBuilder.append(" ");
            slotTimeBuilder.append(slotTimingModel.getToDate());

            holder.binding.slotTimeTxt.setText(slotTimeBuilder.toString());

            holder.binding.removeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    slotTimingModels.remove(position);
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
        return slotTimingModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemSlotTimeBinding binding;

        MyViewHolder(ItemSlotTimeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
