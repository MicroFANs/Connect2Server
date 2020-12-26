package com.example.dpf_client.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dpf_client.Util.ImageIdUtil;
import com.example.dpf_client.Util.Record;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

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