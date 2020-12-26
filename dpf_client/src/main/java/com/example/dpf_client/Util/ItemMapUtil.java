package com.example.dpf_client.Util;

import java.util.HashMap;

public class ItemMapUtil {
    //根据key得到项name以及imageid
    public static String getNameByKey(HashMap<Integer,String> hashMap,int key){
        return hashMap.get(key);
    }

}
