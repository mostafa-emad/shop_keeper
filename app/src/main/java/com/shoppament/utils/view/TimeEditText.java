package com.shoppament.utils.view;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.shoppament.R;
import com.shoppament.databinding.LayoutTimeEditTextBinding;
import com.shoppament.utils.TimeFormatManager;

import java.util.Calendar;
import java.util.Date;

public class TimeEditText extends ConstraintLayout {
    private LayoutTimeEditTextBinding binding;
    private Calendar calendar;

    public TimeEditText(Context context) {
        super(context);
        initView();
    }

    public TimeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TimeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from
                (getContext()), R.layout.layout_time_edit_text,this,true);

        binding.timePickerImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePiker();
            }
        });
    }

    public void showTimePiker() {
        if(calendar == null)
            calendar = Calendar.getInstance();

        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                calendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                calendar.set(Calendar.MINUTE,selectedMinute);
                binding.timeEt.setText(TimeFormatManager.getInstance().format24Hours(calendar.getTime()));
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    public String getTimeValue(){
        return binding.timeEt.getText().toString();
    }

    public String getTime12Hours(){
        return TimeFormatManager.getInstance().format12Hours(binding.timeEt.getText().toString());
    }

    public String getTime24Hours(){
        return TimeFormatManager.getInstance().format24Hours(binding.timeEt.getText().toString());
    }

    public int[] getTimeHhMm(){
        String timeValue = getTimeValue();
        if(!timeValue.isEmpty())
            return TimeFormatManager.getInstance().getHhMm(timeValue);
        return new int[]{0,0};
    }

    public int getTimeMinutes(){
        String timeValue = getTimeValue();
        if(!timeValue.isEmpty())
            return TimeFormatManager.getInstance().getMinutesFromHhMm(timeValue);
        return 0;
    }

    public void setTime(String time){
        binding.timeEt.setText(time);
    }

    public void setTime(Date time){
        binding.timeEt.setText(TimeFormatManager.getInstance().format24Hours(time));
    }

    public void setHint(String hint){
        binding.timeEt.setHint(hint);
    }
}
