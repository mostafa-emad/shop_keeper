package com.shoppament.utils.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.shoppament.R;
import com.shoppament.ui.adapters.PopupOptionsListAdapter;
import com.shoppament.utils.callbacks.OnAlertDialogListener;
import com.shoppament.utils.callbacks.OnObjectChangedListener;

import java.util.ArrayList;
import java.util.Objects;

public class ViewController {
    private static ViewController viewController;
    private PopupWindow popupWindow;

    public static ViewController getInstance() {
        if (viewController == null) {
            synchronized (ViewController.class) {
                ViewController manager = viewController;
                if (manager == null) {
                    synchronized (ViewController.class) {
                        viewController = new ViewController();
                    }
                }
            }
        }
        return viewController;
    }

    public void showAlertDialog(Context context, String title, String message, final OnAlertDialogListener onAlertDialogListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(onAlertDialogListener!=null)
                            onAlertDialogListener.onOkClicked(0);
                    }
                }).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(onAlertDialogListener!=null)
                    onAlertDialogListener.onCancelClicked(0);
            }
        }).show();
    }

    public void showAlertDialog(Context context, final int id, String title, String message, final OnAlertDialogListener onAlertDialogListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(onAlertDialogListener!=null)
                            onAlertDialogListener.onOkClicked(id);
                    }
                }).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(onAlertDialogListener!=null)
                    onAlertDialogListener.onCancelClicked(id);
            }
        }).show();
    }

    public void showAlertDialog(Context context, final int id, String title, String message, final OnAlertDialogListener onAlertDialogListener, String cancel) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(onAlertDialogListener!=null)
                            onAlertDialogListener.onOkClicked(id);
                    }
                }).setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(onAlertDialogListener!=null)
                    onAlertDialogListener.onCancelClicked(id);
            }
        }).show();
    }

    public void showAlertDialog(Context context, final int id, String title, String message, final OnAlertDialogListener onAlertDialogListener, String ok, String cancel) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(onAlertDialogListener!=null)
                            onAlertDialogListener.onOkClicked(id);
                    }
                }).setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(onAlertDialogListener!=null)
                    onAlertDialogListener.onCancelClicked(id);
            }
        }).show();
    }

    public void showAlertDialog(Context context, String title, String message, final OnAlertDialogListener onAlertDialogListener, String ok) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(onAlertDialogListener!=null)
                            onAlertDialogListener.onOkClicked(0);
                    }
                }).show();
    }


    /**
     * show popup window method reuturn PopupWindow
     */
    public PopupWindow showPopupWindow(Context context, final ArrayList<String> dataList, final OnObjectChangedListener onItemClickListener) {
        // initialize a pop up window type
        popupWindow = new PopupWindow(context);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, dataList);
        ListView listViewSort = new ListView(context);
        listViewSort.setAdapter(adapter);
        // set on item selected
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickListener!=null) {
                    onItemClickListener.onObjectChanged(position,position,dataList.get(position));
                }
                popupWindow.dismiss();
            }
        });
        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(500);
        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_bg));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the listview as popup content
        popupWindow.setContentView(listViewSort);

        return popupWindow;
    }

    public void showMultipleActionsDialog(final Context context, String msg, String action1, String action2, final OnObjectChangedListener onObjectChangedListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_actions_dialog, null);
        ((TextView)view.findViewById(R.id.msg_txt)).setText(msg);
        Button okBtn = view.findViewById(R.id.ok_btn);
        Button cancelBtn = view.findViewById(R.id.cancel_btn);

        okBtn.setText(action1);
        cancelBtn.setText(action2);

        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog alert =builder.create();
        buildAlert(context,alert,pxFromDp(context,450), ViewGroup.LayoutParams.WRAP_CONTENT);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onObjectChangedListener!=null)
                    onObjectChangedListener.onObjectChanged(1,0,null);
                alert.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onObjectChangedListener!=null)
                    onObjectChangedListener.onObjectChanged(0,0,null);
                alert.dismiss();
            }
        });
    }


    private PopupWindow showListPopupWindow(Context context, final ArrayList<String> models, final OnObjectChangedListener onItemClickListener) {
        // initialize a pop up window type
        popupWindow = new PopupWindow(context);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, models);
        ListView listViewSort = new ListView(context);
        listViewSort.setAdapter(adapter);
        // set on item selected
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickListener!=null) {
                    onItemClickListener.onObjectChanged(position,position,models.get(position));
                }
                popupWindow.dismiss();
            }
        });
        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(300);
//        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_bg));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the listview as popup content
        popupWindow.setContentView(listViewSort);

        return popupWindow;
    }

    public PopupWindow showOptionsPopupWindow(Activity activity, final ArrayList<String> list, int layout, final OnObjectChangedListener onObjectChangedListener) {
        // initialize a pop up window type
        popupWindow = new PopupWindow(activity);
        final PopupOptionsListAdapter popupOptionsListAdapter =new PopupOptionsListAdapter(activity,list,layout);

        ListView listViewSort = new ListView(activity);
//        listViewSort.setBackground(activity.getResources().getDrawable(R.drawable.pop_bg));
//        listViewSort.setBackground(activity.getResources().getDrawable(R.drawable.gray_round_border));
        listViewSort.setPadding(0,pxFromDp(activity,10),0,0);
        listViewSort.setAdapter(popupOptionsListAdapter);
        // set on item selected
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onObjectChangedListener!=null) {
                    onObjectChangedListener.onObjectChanged(position,position,list.get(position));
                }
                popupWindow.dismiss();
            }
        });
        // some other visual settings for popup window
        popupWindow.setFocusable(true);
//        popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.popup_background));
        popupWindow.setWidth(pxFromDp(activity,150));
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listViewSort);

        return popupWindow;
    }

    public PopupWindow showOptionsPopupWindow(Activity activity, final ArrayList<String> list, final OnObjectChangedListener onObjectChangedListener) {
        // initialize a pop up window type
        popupWindow = new PopupWindow(activity);
        final PopupOptionsListAdapter popupOptionsListAdapter =new PopupOptionsListAdapter(activity,list);

        ListView listViewSort = new ListView(activity);
//        listViewSort.setBackground(activity.getResources().getDrawable(R.drawable.pop_bg));
//        listViewSort.setBackground(activity.getResources().getDrawable(R.drawable.gray_round_border));
//        listViewSort.setPadding(0,pxFromDp(activity,10),0,0);
        listViewSort.setAdapter(popupOptionsListAdapter);
        // set on item selected
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onObjectChangedListener!=null) {
                    onObjectChangedListener.onObjectChanged(position,position,list.get(position));
                }
                popupWindow.dismiss();
            }
        });
        // some other visual settings for popup window
        popupWindow.setFocusable(true);
//        popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.popup_background));
        popupWindow.setWidth(pxFromDp(activity,100));
        popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.popup_bg));
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listViewSort);

        return popupWindow;
    }

    private void buildAlert(Context context, AlertDialog alert, int width, int height) {
        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alert.show();

        alert.getWindow().setLayout(width, height);
    }

    private void buildAlert(AlertDialog alert, int width, int height) {
//        alert.getWindow().setDimAmount(0f);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alert.show();

        alert.getWindow().setLayout(width, height);
    }

    public void showDialog(final Context context, String text) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_action_dialog, null);
        ((TextView)view.findViewById(R.id.msg_txt)).setText(text);
        Button okBtn = (Button)view.findViewById(R.id.ok_txt);
//        okBtn.setText(LocalizationManager.getInstance().getTranslationValueOf(TranslationDictionaryKeys.OK,context.getResources().getString(R.string.OK)));

        builder.setCancelable(true);
        builder.setView(view);
        final AlertDialog alert =builder.create();
        buildAlert(context,alert,pxFromDp(context,450), ViewGroup.LayoutParams.WRAP_CONTENT);

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                alert.dismiss();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    public void showDialog(final Context context, String text, final OnObjectChangedListener onObjectChangedListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_action_dialog, null);
        ((TextView)view.findViewById(R.id.msg_txt)).setText(text);
        Button okBtn = (Button)view.findViewById(R.id.ok_txt);
//        okBtn.setText(LocalizationManager.getInstance().getTranslationValueOf(TranslationDictionaryKeys.OK,context.getResources().getString(R.string.OK)));

        builder.setCancelable(true);
        builder.setView(view);
        final AlertDialog alert =builder.create();
        buildAlert(context,alert,pxFromDp(context,450), ViewGroup.LayoutParams.WRAP_CONTENT);

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                alert.dismiss();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onObjectChangedListener!=null)
                    onObjectChangedListener.onObjectChanged(0,0,alert);
                alert.dismiss();
            }
        });
    }

    public void showUploadOptionsDialog(final Context context, final OnObjectChangedListener onObjectChangedListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_upload_options_dialog, null);

        TextView cameraOptionTxt = view.findViewById(R.id.camera_option_txt);
        TextView deviceOptionTxt = view.findViewById(R.id.device_option_txt);

        builder.setCancelable(true);
        builder.setView(view);
        final AlertDialog alert =builder.create();

        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams manager = Objects.requireNonNull(alert.getWindow()).getAttributes();
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
                if(onObjectChangedListener!=null)
                    onObjectChangedListener.onObjectChanged(0,0,alert);
                alert.dismiss();
            }
        });

        deviceOptionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onObjectChangedListener!=null)
                    onObjectChangedListener.onObjectChanged(0,0,alert);
                alert.dismiss();
            }
        });
    }

    private int pxFromDp(Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

}
