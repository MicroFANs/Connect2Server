package com.example.dpf_client.Util;

import java.io.Serializable;

//可以序列化，用来转为json用SharedPreferences保存
public class Record implements Serializable {
    private int key;
    private String name;
    private int imgId;
    private double value;

    public Record(int key, String name, int imgId, double value) {
        this.key = key;
        this.name = name;
        this.imgId = imgId;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
