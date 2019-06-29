package com.m.ws2;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class MyJsonArray {
    private String TAG = "MyJsonArray";
    private List<MyJsonObject> mList = new ArrayList<>();

    public MyJsonArray(String data) {
        if(data ==null || data.equals("")){
            Log.e(TAG,"data is null object");
            //return;
        }
        char[] str = data.toCharArray();
        int maxCount = 0;
        int qCount = 0;
        int i = 0, length = data.length();
        int start = 0;
        while (i < length) {
            switch (str[i]) {
                case '[':
                case ']':
                    break;
                case '\'':
                case '\"':
                    qCount = qCount == 0 ? 1 : 0;
                    break;
                case '{':
                    if (qCount == 1) {
                        break;
                    }
                    maxCount++;
                    if (maxCount == 1)
                        start = i;
                    break;
                case '}':
                    if (qCount == 1) {
                        break;
                    }
                    maxCount--;
                    if (maxCount == 0) {
                        MyJsonObject myJsonObject = new MyJsonObject(data.substring(start, i + 1));
                        mList.add(myJsonObject);
                    }
                    break;
                default:
                    break;
            }
            i++;
        }
    }

    public MyJsonArray(List<MyJsonObject> list) {
        if (list != null) {
            mList = list;
        }
    }

    public MyJsonObject get(int index) {
        return mList.get(index);
    }

    public List<MyJsonObject> getList(){
        return mList;
    }
}
