package com.example.dpf_client.Util;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    /**
     * 连接
     *
     * @param client
     * @param url
     * @param callback 回调函数
     */
    public static void sendOkHttpRequest(OkHttpClient client, String url, okhttp3.Callback callback) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * @param client
     * @param url
     * @param formBody 要将kv数据封装在该参数中
     * @param callback 回调函数
     */
    public static void sendOkHttpRequest(OkHttpClient client, String url, FormBody formBody, okhttp3.Callback callback) {
        Request request = new Request.Builder().url(url).post(formBody).build();
        client.newCall(request).enqueue(callback);

    }


}
