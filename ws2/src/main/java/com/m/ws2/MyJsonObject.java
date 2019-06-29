package com.m.ws2;

/**
 * @author Frontman
 * @version 1.0
 * @date 2019/06/06
 **/

public class MyJsonObject {
    private String JSON;

    public MyJsonObject(String JSON) {
        this.JSON = JSON;
    }

    public void setJson(String JSON) {
        this.JSON = JSON;
    }

    public MyJsonObject getJsonObject(String name) {
        MyJsonObject temp = new MyJsonObject(get(name));
        return temp;
    }

    public MyJsonArray getJsonArray(String name) {
        String tmp = get(name);
        return new MyJsonArray(tmp);
    }

    public int getInt(String name) {
        String tmp = get(name);
        if(tmp == null||tmp.equals("null"))
            return 0;
        else {
            //Log.e("tmp",tmp);
            return Integer.parseInt(tmp);
        }
    }

    public long getLong(String name) {
        String tmp = get(name);
        return tmp=="null" ? 0 : Long.parseLong(tmp);
    }

    public double getDouble(String name) {
        String tmp = get(name);
        return tmp== "null" ? 0 : Double.parseDouble(tmp);
    }

    public String getString(String name) {
        return get(name);
    }

    public String toString() {
        return this.JSON;
    }

    private String get(String name) {
        if (JSON == null)
            return null;
        int length = JSON.length(), i = 0;
        char str[] = JSON.toCharArray();
        int maxCount = 0;
        int midCount = 0;
        while (i < length) {
            //Log.e("iii",str[i]+"");
            switch (str[i]) {
                case '{':
                    maxCount++;
                    break;
                case '}':
                    maxCount--;
                    break;
                case '[':
                    midCount++;
                    break;
                case ']':
                    midCount--;
                    break;
                case '\'':
                case '\"': {
                    if (maxCount != 1 || midCount != 0)
                        break;
                    String name_tmp = "";
                    while (i < length) {
                        i++;
                        if (str[i] == '\'' || str[i] == '\"')
                            break;
                        name_tmp += str[i];
                    }
                    if (name.equals(name_tmp)) {
                        int count_1 = 0;
                        int count_2 = 0;
                        int count_3 = 0;
                        int count_4 = 0;
                        int temp = 0;
                        while (++i < length) {
                            switch (str[i]) {
                                case ':':
                                    if (count_3 == 0)
                                        temp = i + 1;
                                    count_3++;
                                    break;
                                case '\'':
                                case '\"':
                                    if (count_4 == 0 && count_1 == 0 && count_2 == 0) {
                                        count_4 = 1;
                                        temp = i + 1;
                                        break;
                                    } else if (count_4 == 1 && count_1 == 0 && count_2 == 0) {
                                        return JSON.substring(temp, i);
                                    }
                                    break;
                                case '{':
                                    count_1++;
                                    break;
                                case '[':
                                    count_2++;
                                    break;
                                case '}':
                                    if (count_1 == 0) {
                                        if (--maxCount == 0) {
                                            return JSON.substring(temp, i);
                                        }
                                    }
                                    count_1--;
                                    if (count_1 == 0 && count_2 == 0)
                                        return JSON.substring(temp, i + 1);
                                    break;
                                case ']':
                                    count_2--;
                                    if (count_2 == 0 && count_1 == 0)
                                        return JSON.substring(temp, i + 1);
                                    break;
                                case ',':
                                    count_3--;
                                    if (count_1 == 0 && count_2 == 0 && count_3 == 0)
                                        return JSON.substring(temp, i);
                                    break;
                            }
                        }
                    }
                    i++;
                    break;
                }
                default:
                    break;
            }
            i++;
        }
        return null;
    }
}

