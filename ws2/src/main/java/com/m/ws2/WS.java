package com.m.ws2;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @author Frontman
 * @date 2019/06/29
 * 试试看链式调用
 **/
public class WS {
    private static final String TAG = "WS_OkHttp";
    private String mUrl = null;
    private CallBack mCallBack = null;        //接口回调
    private OkHttpClient mOkHttp = null;
    private String[][] mParam = new String[][]{};      //用来接收额外的请求参数，post
    private String[][] mHeader = new String[][]{};     //设置请求头
    private static HashMap<String, List<Cookie>> cookieStore = new HashMap<>();//Cookies

    public static WS load(String url) {
        if (!url.equals(null))
            return new WS().setmUrl(url);
        return null;
    }

    private WS setmUrl(String url){
        mUrl = url;
        return this;
    }
    public WS saveCookies(final String cookName) {
        mOkHttp = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(cookName, cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                return new ArrayList<Cookie>();
            }
        }).build();

        return WS.this;
    }

    public WS loadCookies(final String cookName) {
        mOkHttp = new OkHttpClient().newBuilder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(cookName);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).build();
        return this;
    }

    /**
     * @param header 每个一维数组中
     *               0 name
     *               1 value
     **/
    public WS addHeader(String[][] header) {
        mHeader = header;
        return this;
    }

    /**
     * @param param 每个一维数组中
     *              0 name
     *              1 value
     **/
    public WS addParam(String[][] param) {
        mParam = param;
        return this;
    }

    public WS addCallBack(CallBack callBack) {
        mCallBack = callBack;
        return this;
    }

    public void post() {
        if (mOkHttp == null)
            mOkHttp = new OkHttpClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request.Builder builder = new Request.Builder();
                Request request;
                if (mHeader != null) {
                    for (String[] temp : mHeader) {
                        builder.addHeader(temp[0], temp[1]);
                    }
                }
                request = builder.post(getFormBody()).url(mUrl).build();
                try {
                    Response response = mOkHttp.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Result code:" + response.code() + "\tMessage:" + response.message());
                        if (mCallBack != null)
                            //这里有坑，body().string()方法第二次调用导致数据为空
                            mCallBack.onResult(response.body().string());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "未知错误!");
                }
            }
        }).start();

    }

    public void get() {
        if (mOkHttp == null)
            mOkHttp = new OkHttpClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request.Builder builder = new Request.Builder();
                Request request;
                if (mHeader != null) {
                    for (String[] temp : mHeader) {
                        builder.addHeader(temp[0], temp[1]);
                    }
                }
                request = builder.url(mUrl).build();
                Response response = null;
                try {
                    response = mOkHttp.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Result code:" + response.code() + "\tMessage:" + response.message());
                        if (mCallBack != null)
                            mCallBack.onResult(response.body().string());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "未知错误!");
                }

            }
        }).start();
    }

    private FormBody getFormBody() {
        if (mParam == null) {
            mParam = new String[][]{};
            Log.e(TAG, "Param is null");
        }
        FormBody.Builder formBody = new FormBody.Builder();
        for (String[] temp : mParam) {
            formBody.add(temp[0], temp[1]);
        }
        return formBody.build();
    }

    interface CallBack {
        void onResult(String Json);
    }
}