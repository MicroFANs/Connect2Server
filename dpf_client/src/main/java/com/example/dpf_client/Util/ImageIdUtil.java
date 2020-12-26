package com.example.dpf_client.Util;

import java.lang.reflect.Field;

public class ImageIdUtil {
    //使用反射的方式根据用户名来获取图片
    public static int getImageByReflect(String imgName){
        try {
            Field field =Class.forName("com.example.dpf_client.R$drawable").getField(imgName);
            int imgId=field.getInt(field);
            return imgId;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
