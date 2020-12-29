package com.example.dpf_client.Util;

public class Location {
    private int id;
    private float x;
    private float y;
    private int flag;

    public Location(int id, float x, float y, int flag) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
