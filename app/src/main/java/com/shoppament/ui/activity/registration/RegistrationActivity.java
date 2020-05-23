package com.shoppament.ui.activity.registration;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.shoppament.R;
import com.shoppament.ui.base.BaseActivity;
import com.shoppament.utils.view.UploadFileController;

public class RegistrationActivity extends BaseActivity {
    private RegistrationFragment registrationFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registrationFragment = new RegistrationFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_fragment, registrationFragment)
                .commit();
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void doCreate() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case UploadFileController.CAPTURE_PHOTO_ID:
            case UploadFileController.UPLOAD_PHOTO_ID:
                registrationFragment.handleUploadPicture(requestCode,data);
                break;
            case UploadFileController.PERMISSIONS_CAMERA_REQUEST_CODE:
            case UploadFileController.PERMISSIONS_FILES_REQUEST_CODE:
                registrationFragment.clickToUploadPics();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (UploadFileController.getInstance().getPermissions().areAllRequiredPermissionsGranted(permissions, grantResults)){
                registrationFragment.clickToUploadPics();
        }else{
            Toast.makeText(getApplicationContext(),"Permissions Error ", Toast.LENGTH_LONG).show();
        }
    }
}
