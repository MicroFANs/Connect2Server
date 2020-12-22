package com.example.connect2server;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static void showToast(Context context,String content){
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }

    public static String map2Json(Map map){
        Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;
    }

    public static HashMap<Integer,Double> json2Map(String json){
        Gson gson=new Gson();
        HashMap<Integer, Double> map= gson.fromJson(json,new TypeToken<HashMap<Integer,Double>>(){}.getType());
        return map;
    }

    public static HashMap<Integer,Integer> json2IntegerMap(String json){
        Gson gson=new Gson();
        HashMap<Integer, Integer> map= gson.fromJson(json,new TypeToken<HashMap<Integer,Integer>>(){}.getType());
        return map;
    }

}
