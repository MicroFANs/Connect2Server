package com.example.dpf_client.ui.home;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dpf_client.Util.ImageIdUtil;
import com.example.dpf_client.Util.Record;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private static int i = 0;
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
    public ArrayList<Record> getRecords() {
        ArrayList<Record> recordsList = new ArrayList<>();
        int[] keys = {8, 142, 5657, 1449, 5017, 1926, 5137, 5816, 2285, 5843, 3684, 48, 2991, 89, 1973, 314,
                21, 260, 5000, 2811, 101};
        String[] names = {"baseball_cap", "green_tank_top", "red_sports_jersey", "tie_dye_tshirt",
                "blue_tank_top", "swimming_shorts", "green_stripped_tshir", "red_snazzy_shorts",
                "red_dress", "black_turtleneck", "pink_tshirt", "red_sweater", "blue_dress",
                "yellow_skirt", "yellow_dress", "green_dress", "purple_running_shoe", "green_boot",
                "yellow_sandal", "red_high_heel", "orange_tshirt"};
        double[] values = {8, 8, 8, 8, 6, 8, 10, 10, 8, 10, 10, 10, 6, 8, 10, 10, 10, 10, 10, 8, 10};
        for (int i = 0; i < 21; i++) {
            recordsList.add(new Record(keys[i], names[i], ImageIdUtil.getImageByReflect(names[i]), values[i]));
        }
        return recordsList;
    }

    public Record getSingleRecord() {
        int[] keys = {1934, 2287, 926, 1712, 533, 3850, 485, 3284, 2122};
        String[] names = {"trilby", "beanie", "sun_hat", "bowtie", "green_bra", "sock", "scarf", "watch", "sunglasses"};
        double[] values = {8, 10, 6, 4, 10, 8, 10, 10, 8};
        if (i > 8) {
            i = 0;
        }
        Record r = new Record(keys[i], names[i], ImageIdUtil.getImageByReflect(names[i]), values[i]);
        i++;
        return r;
    }
}