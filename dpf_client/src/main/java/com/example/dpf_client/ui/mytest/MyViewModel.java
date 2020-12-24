package com.example.dpf_client.ui.mytest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public MyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is new fragment");//根据自己喜欢放界面的测试文字
    }

    public LiveData<String> getText() {
        return mText;
    }
}
