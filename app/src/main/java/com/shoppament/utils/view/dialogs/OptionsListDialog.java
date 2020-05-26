package com.shoppament.utils.view.dialogs;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppament.R;
import com.shoppament.ui.adapters.OptionsRecyclerAdapter;
import com.shoppament.utils.callbacks.OnOptionsListener;
import com.shoppament.utils.callbacks.OnTaskCompletedListener;

import java.util.List;

public class OptionsListDialog extends BaseCustomDialog {
    private String title;
    private List<String> data;

    public OptionsListDialog(Activity activity, String title, List<String> data, OnTaskCompletedListener onTaskCompletedListener) {
        super(activity, R.layout.layout_options_dialog, onTaskCompletedListener);
        if(isDialogShown())
            return;

        this.title = title;
        this.data = data;
        init();
    }

    @Override
    protected void init() {
        super.init();
        TextView titleTxt = rootView.findViewById(R.id.title_txt);
        TextView closeBtn = rootView.findViewById(R.id.close_btn);
        RecyclerView optionsRecycler = rootView.findViewById(R.id.options_recycler);

        manager.windowAnimations = R.style.DialogTheme;
        alert.show();
        alert.getWindow().setLayout(pxFromDp(300), ViewGroup.LayoutParams.WRAP_CONTENT);

        titleTxt.setText(title);

        optionsRecycler.setLayoutManager(new LinearLayoutManager(activity));
        OptionsRecyclerAdapter optionsRecyclerAdapter = new OptionsRecyclerAdapter(data,activity);
        optionsRecyclerAdapter.setOptionsListener(new OnOptionsListener() {
            @Override
            public void onSelected(String value, int index) {
                if(onTaskCompletedListener!=null){
                    onTaskCompletedListener.onCompleted(value);
                }
                alert.dismiss();
            }
        });
        optionsRecycler.setAdapter(optionsRecyclerAdapter);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }
}
