package com.example.dpf_client.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dpf_client.StartActivity;
import com.example.dpf_client.Util.ImageIdUtil;
import com.example.dpf_client.Util.Record;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private SharedPreferences mReadSP; //读取记录
    private SharedPreferences.Editor mEditorSP; //保存记录


    //Model中的方法
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    //设置在页面上显示的数据源
    public ArrayList<Record> getRecords(){
        ArrayList<Record> recordsList=new ArrayList<>();
        for (int j = 0; j <16 ; j++) {
            String name="icon"+(j+1)%4;
            Record record=new Record(j,name, ImageIdUtil.getImageByReflect(name),j-0.4);
            recordsList.add(record);
        }
        return recordsList;
    }
}