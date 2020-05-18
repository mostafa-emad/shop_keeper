package com.shoppament.utils.view.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoppament.R;
import com.shoppament.utils.callbacks.OnObjectChangedListener;
import com.shoppament.utils.view.UploadFileController;

public class UploadOptionsDialog extends BaseCustomDialog {

    public UploadOptionsDialog(Activity activity) {
        super(activity, R.layout.layout_upload_options_dialog, null);
        init();
    }

    public UploadOptionsDialog(Activity activity, OnObjectChangedListener onObjectChangedListener) {
        super(activity, R.layout.layout_upload_options_dialog, onObjectChangedListener);
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

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                alert.dismiss();
            }
        });

        cameraOptionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadFileController.getInstance().capturePicture(activity);
                if(onObjectChangedListener!=null)
                    onObjectChangedListener.onObjectChanged(0,0,alert);
                alert.dismiss();
            }
        });

        deviceOptionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadFileController.getInstance().uploadPictureFromDevice(activity);
                if(onObjectChangedListener!=null)
                    onObjectChangedListener.onObjectChanged(0,0,alert);
                alert.dismiss();
            }
        });
    }

}
