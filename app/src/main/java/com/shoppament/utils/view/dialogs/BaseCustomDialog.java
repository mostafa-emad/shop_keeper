package com.shoppament.utils.view.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.shoppament.utils.callbacks.OnTaskCompletedListener;

import java.util.Objects;

public class BaseCustomDialog implements DialogInterface.OnDismissListener {
    OnTaskCompletedListener onTaskCompletedListener;
    protected Activity activity;
    private int layout;
    private AlertDialog.Builder builder;
    View rootView;
    AlertDialog alert;
    private boolean isCancelEnabled = true;
    private static boolean isShown = false;
    protected WindowManager.LayoutParams manager;

    BaseCustomDialog(Activity activity, int layout, OnTaskCompletedListener onTaskCompletedListener) {
        this.onTaskCompletedListener = onTaskCompletedListener;
        this.layout = layout;
        this.activity = activity;
    }

    protected void init() {
        builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        rootView = inflater.inflate(layout, null);

        builder.setView(rootView);
        alert =builder.create();
        alert.setCancelable(isCancelEnabled);
        alert.setOnDismissListener(this);

        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        manager = Objects.requireNonNull(alert.getWindow()).getAttributes();
    }

    int pxFromDp(float dp) {
        return (int) (dp * activity.getResources().getDisplayMetrics().density);
    }

    boolean isDialogShown(){
        if(isShown) {
            return true;
        }
        isShown = true;
        return false;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        isShown = false;
    }
}
