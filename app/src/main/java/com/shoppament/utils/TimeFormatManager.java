package com.shoppament.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class TimeFormatManager {
    private SimpleDateFormat simpleDateFormat24Hours = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat simpleDateFormat12Hours = new SimpleDateFormat("hh:mm a");

    private static TimeFormatManager timeFormatManager;

    public static TimeFormatManager getInstance() {
        if (timeFormatManager == null) {
            synchronized (TimeFormatManager.class) {
                TimeFormatManager manager = timeFormatManager;
                if (manager == null) {
                    synchronized (TimeFormatManager.class) {
                        timeFormatManager = new TimeFormatManager();
                    }
                }
            }
        }
        return timeFormatManager;
    }

    public int getMinutesFromHhMm(String hours, String minutes){
        int hh = 0;
        int mm = 0;
        if(!hours.isEmpty()){
            hh = Integer.parseInt(hours);
        }
        if(!minutes.isEmpty()){
            mm = Integer.parseInt(minutes);
        }
        return hh * 60 + mm;
    }

    public int getMinutesFromHhMm(String timeValue){
        int[] timeHhMm = TimeFormatManager.getInstance().getHhMm(timeValue);
        return timeHhMm[0] * 60 + timeHhMm[1];
    }

    public int[] getHhMmFromMinutes(int minutes){
        int [] timeHhMm = {0,0};
        if(minutes > 60){
            timeHhMm[0] = minutes / 60;
            timeHhMm[1] = minutes % 60;
        }else {
            timeHhMm[1] = minutes;
        }
        return timeHhMm;
    }

    public int[] getHhMm(String time){
        String[] timeArray = time.split(":");
        return new int[]{ Integer.parseInt(timeArray[0]) , Integer.parseInt(timeArray[1])};
    }

    private String format24HhMm(int hours, int minutes){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR,hours);
        calendar.set(Calendar.MINUTE,minutes);

        return simpleDateFormat24Hours.format(calendar.getTime());
    }

    private String format12HhMm(int hours, int minutes){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR,hours);
        calendar.set(Calendar.MINUTE,minutes);

        return simpleDateFormat24Hours.format(calendar.getTime());
    }

    public String format24Hours(Date time){
        return simpleDateFormat24Hours.format(time);
    }

    public String format12Hours(Date time){
        return simpleDateFormat12Hours.format(time);
    }

    public String format24Hours(String time){
        if(time.isEmpty())
            return "";

        int[] timeHhMm = getHhMm(time);
        return format24HhMm(timeHhMm[0],timeHhMm[1]);
    }

    public String format12Hours(String time){
        if(time.isEmpty())
            return "";

        int[] timeHhMm = getHhMm(time);
        return format12HhMm(timeHhMm[0],timeHhMm[1]);
    }

    public String format12Hours(int minutesTime) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat12Hours.setTimeZone(timeZone);
        return  simpleDateFormat12Hours.format(new Date(minutesTime * 60 * 1000L));
    }

}
