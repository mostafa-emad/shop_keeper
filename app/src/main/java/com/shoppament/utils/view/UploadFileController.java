package com.shoppament.utils.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.mindorks.paracamera.Camera;
import com.shoppament.data.models.PictureModel;
import com.shoppament.utils.FileUtils;

public class UploadFileController {
    public static final int CAPTURE_PHOTO_ID = 1001;
    public static final int UPLOAD_PHOTO_ID = 1002;
    private static UploadFileController controller;
    private Camera camera;

    public static UploadFileController getInstance() {
        if (controller == null) {
            synchronized (UploadFileController.class) {
                UploadFileController manager = controller;
                if (manager == null) {
                    synchronized (UploadFileController.class) {
                        controller = new UploadFileController();
                    }
                }
            }
        }
        return controller;
    }

    public void capturePicture(Activity activity){
        try {
            if(camera == null)
                initCamera(activity);

            camera.takePicture();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void uploadPictureFromDevice(Activity activity){
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            activity.startActivityForResult(intent, UPLOAD_PHOTO_ID);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initCamera(Activity activity) {
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(CAPTURE_PHOTO_ID)
                .setDirectory("pics")
                .setName("pic_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(activity);
    }

    private void deleteCameraImg(){
        if(camera != null)
            camera.deleteImage();
    }

    public Bitmap getCameraPictureSource() {
        return camera.getCameraBitmap();
    }

    public PictureModel getCameraPicture() {
        try {
            PictureModel pictureModel = new PictureModel();
            String path = camera.getCameraBitmapPath();
            pictureModel.setPath(path);
            pictureModel.setName(path.substring(path.lastIndexOf("/")+1));

            return pictureModel;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getDevicePictureSource(Intent data,Activity activity) {
        try {
            Uri uri = data.getData();
            return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PictureModel getDevicePicture(Intent data, Activity activity) {
        try {
            Uri uri = data.getData();
            if(uri == null)
                return null;

            PictureModel pictureModel = new PictureModel();
            String path = FileUtils.getPath(activity,uri);
            pictureModel.setPath(path);
            assert path != null;
            pictureModel.setName(path.substring(path.lastIndexOf("/")+1));

            return pictureModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
