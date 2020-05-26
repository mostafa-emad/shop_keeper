package com.shoppament.utils.view.dialogs;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppament.R;
import com.shoppament.utils.callbacks.OnTaskCompletedListener;
import com.shoppament.utils.view.UploadFileController;

public class UploadOptionsDialog extends BaseCustomDialog {

    public UploadOptionsDialog(Activity activity) {
        super(activity, R.layout.layout_upload_options_dialog, null);
        if(isDialogShown())
            return;
        init();
    }

    public UploadOptionsDialog(Activity activity, OnTaskCompletedListener onTaskCompletedListener) {
        super(activity, R.layout.layout_upload_options_dialog, onTaskCompletedListener);
        if(isDialogShown())
            return;
        init();
    }

    @Override
    protected void init() {
        super.init();
        TextView cameraOptionTxt = rootView.findViewById(R.id.camera_option_txt);
        TextView deviceOptionTxt = rootView.findViewById(R.id.device_option_txt);

        manager.gravity = Gravity.BOTTOM;
        manager.windowAnimations = R.style.DialogBottomTheme;

        alert.show();
        alert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cameraOptionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UploadFileController.getInstance().isMediaPermissionsEnabled(activity)){
                    UploadFileController.getInstance().capturePicture(activity);
                    if(onTaskCompletedListener!=null)
                        onTaskCompletedListener.onCompleted(alert);
                }
//                if(!permissions.checkPermissions()){
//                    if(onTaskCompletedListener!=null)
//                        onTaskCompletedListener.onError(0,activity.getResources().getString(R.string.error_upload_services_disabled));
//                    permissions.requestPermissions(PERMISSIONS_REQUEST_CODE);
//                }
                alert.dismiss();
            }
        });

        deviceOptionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UploadFileController.getInstance().isMediaPermissionsEnabled(activity)){
                    UploadFileController.getInstance().uploadPictureFromDevice(activity);
                    if(onTaskCompletedListener!=null)
                        onTaskCompletedListener.onCompleted(alert);
                }
//                if(!permissions.checkPermissions()){
//                    if(onTaskCompletedListener!=null)
//                        onTaskCompletedListener.onError(0,activity.getResources().getString(R.string.error_upload_services_disabled));
//                }else{
//                    if(onTaskCompletedListener!=null)
//                        onTaskCompletedListener.onCompleted(alert);
//                }
                alert.dismiss();
            }
        });
    }
}
