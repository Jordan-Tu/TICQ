package com.tu.curriculumdesign.util;

import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.application.MyApplication;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    public static void sendOkHttpRequest(String controller, RequestBody requestBody, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(MyApplication.getContext().getString(R.string.server_ip_add) +MyApplication.getContext().getString(R.string.server_name) + controller)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
