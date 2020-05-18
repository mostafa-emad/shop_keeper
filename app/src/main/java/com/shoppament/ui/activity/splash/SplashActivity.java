package com.shoppament.ui.activity.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.shoppament.R;
import com.shoppament.ui.activity.registration.RegistrationActivity;
import com.shoppament.utils.AndroidPermissions;

public class SplashActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 101;
    private AndroidPermissions mPermissions;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        mPermissions = new AndroidPermissions(this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        );

        if (mPermissions.checkPermissions()) {
            openMainScreen();
        } else {
            mPermissions.requestPermissions(PERMISSIONS_REQUEST_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissions.areAllRequiredPermissionsGranted(permissions, grantResults)) {
            openMainScreen();
        } else {
            Toast.makeText(getApplicationContext(),"Permissions Error ", Toast.LENGTH_LONG).show();
        }
    }

    private void openMainScreen() {
        Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.fade_out);
        finish();
    }
}
