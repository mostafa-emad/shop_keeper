package com.shoppament.utils.view.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppament.R;
import com.shoppament.utils.AndroidPermissions;
import com.shoppament.utils.callbacks.OnTaskCompletedListener;
import com.shoppament.utils.view.UploadFileController;

public class UploadOptionsDialog extends BaseCustomDialog {

    public UploadOptionsDialog(Activity activity) {
        super(activity, R.layout.layout_upload_options_dialog, null);
        init();
    }

    public UploadOptionsDialog(Activity activity, OnTaskCompletedListener onTaskCompletedListener) {
        super(activity, R.layout.layout_upload_options_dialog, onTaskCompletedListener);
        init();
    }

    @Override
    protected void init() {
        super.init();
        TextView cameraOptionTxt = rootView.findViewById(R.id.camera_option_txt);
        TextView deviceOptionTxt = rootView.findViewById(R.id.device_option_txt);

        manager.gravity = Gravity.BOTTOM;
        manager.windowAnimations = R.style.DialogBottomTheme;

        permissions = new AndroidPermissions(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        );

        alert.show();
        alert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                alert.dismiss();
            }
        });

        cameraOptionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!permissions.checkPermissions()){
                    if(onTaskCompletedListener!=null)
                        onTaskCompletedListener.onError(0,activity.getResources().getString(R.string.error_upload_services_disabled));
                }else{
                    UploadFileController.getInstance().capturePicture(activity);
                    if(onTaskCompletedListener!=null)
                        onTaskCompletedListener.onCompleted(alert);
                }
                alert.dismiss();
            }
        });

        deviceOptionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!permissions.checkPermissions()){
                    if(onTaskCompletedListener!=null)
                        onTaskCompletedListener.onError(0,activity.getResources().getString(R.string.error_upload_services_disabled));
                }else{
                    UploadFileController.getInstance().uploadPictureFromDevice(activity);
                    if(onTaskCompletedListener!=null)
                        onTaskCompletedListener.onCompleted(alert);
                }
                alert.dismiss();
            }
        });
    }
}
