package com.example.dpf_client.Util;

public class Record {
    private String id;
    private int imgId;

    public Record(String id, int imgId) {
        this.id = id;
        this.imgId = imgId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
