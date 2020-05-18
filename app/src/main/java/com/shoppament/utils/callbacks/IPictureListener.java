package com.shoppament.utils.callbacks;

import com.shoppament.data.models.PictureModel;

public interface IPictureListener {
    void showSelectedPic(PictureModel pictureModel);
    void deleteSelectedPic(int position);
}
