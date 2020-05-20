package com.shoppament.ui.activity.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

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
        if(requestCode == UploadFileController.CAPTURE_PHOTO_ID || requestCode == UploadFileController.UPLOAD_PHOTO_ID){
            registrationFragment.handleUploadPicture(requestCode,data);
        }
    }
}
