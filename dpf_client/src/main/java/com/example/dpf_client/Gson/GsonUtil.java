package com.example.dpf_client.Gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class GsonUtil {

    public static HashMap<String,String> json2Map(String json){
        Gson gson=new Gson();
        HashMap<String, String> map= gson.fromJson(json,new TypeToken<HashMap<String,String>>(){}.getType());
        return map;
    }

    public static String map2json(HashMap map){
        Gson gson=new Gson();
        String json=gson.toJson(map);
        return json;
    }


}
