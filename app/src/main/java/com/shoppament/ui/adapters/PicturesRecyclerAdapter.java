package com.shoppament.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppament.R;
import com.shoppament.data.models.PictureModel;
import com.shoppament.databinding.ItemPictureBinding;
import com.shoppament.utils.callbacks.IPictureListener;

import java.util.List;

public class PicturesRecyclerAdapter extends BaseRecyclerAdapter{
    private List<PictureModel> pictureModels;
    private IPictureListener iPictureListener;

    public PicturesRecyclerAdapter(List<PictureModel> pictureModels, Activity activity,IPictureListener iPictureListener) {
        this.pictureModels = pictureModels;
        this.activity = activity;
        this.iPictureListener = iPictureListener;
    }

    public void setPictureModels(List<PictureModel> pictureModels) {
        this.pictureModels = pictureModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPictureBinding itemPictureBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_picture,parent,false);
        return new MyViewHolder(itemPictureBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final MyViewHolder holder = (MyViewHolder) viewHolder;
        final PictureModel pictureModel = pictureModels.get(position);
        try{
            holder.binding.pictureNameTxt.setText(pictureModel.getName());
            holder.binding.removePictureImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(iPictureListener!=null){
                        iPictureListener.deleteSelectedPic(position);
                    }
                }
            });
            holder.binding.pictureNameTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(iPictureListener!=null){
                        iPictureListener.showSelectedPic(pictureModel);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return pictureModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemPictureBinding binding;

        MyViewHolder(ItemPictureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
