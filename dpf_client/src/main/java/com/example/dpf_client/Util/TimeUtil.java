package com.example.dpf_client.Util;

import android.icu.text.SimpleDateFormat;

import java.util.Date;

public class TimeUtil {
    //获取当前时间
    public static String getTime(){
        Date date=new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm:ss");
        return dateFormat.format(date);
    }
}
