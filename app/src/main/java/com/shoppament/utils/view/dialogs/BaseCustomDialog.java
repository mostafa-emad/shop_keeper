package com.shoppament.utils.view.dialogs;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.shoppament.utils.callbacks.OnObjectChangedListener;

import java.util.Objects;

public class BaseCustomDialog {
    protected OnObjectChangedListener onObjectChangedListener;
    protected Activity activity;
    private int layout;
    protected AlertDialog.Builder builder;
    protected View rootView;
    protected AlertDialog alert;
    protected boolean isCancelEnabled = true;
    protected WindowManager.LayoutParams manager;

    public BaseCustomDialog(Activity activity, int layout,OnObjectChangedListener onObjectChangedListener) {
        this.onObjectChangedListener = onObjectChangedListener;
        this.layout = layout;
        this.activity = activity;
    }

    protected void init() {
        builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        rootView = inflater.inflate(layout, null);

        builder.setCancelable(true);
        builder.setView(rootView);
        alert =builder.create();

        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        manager = Objects.requireNonNull(alert.getWindow()).getAttributes();
    }

    public int pxFromDp(float dp) {
        return (int) (dp * activity.getResources().getDisplayMetrics().density);
    }
}
