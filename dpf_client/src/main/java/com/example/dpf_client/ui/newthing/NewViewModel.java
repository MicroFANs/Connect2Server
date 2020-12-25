package com.example.dpf_client.ui.newthing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class NewViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mText;
    private Date now;

    public NewViewModel(){
        mText=new MutableLiveData<>();
        mText.setValue("这是新的fragment");
    }

    public LiveData<String> getText(){
        return mText;
    }

    public String getData(){
        now=new Date();
        return now.toString();

    }
}
