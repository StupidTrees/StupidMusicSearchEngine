package com.stupidtree.sse.utils;

import com.alibaba.fastjson.JSON;
import net.minidev.json.JSONObject;

import javax.swing.*;
import java.util.Map;

public class JsonUtils {
    public static JSONObject getJson(Object... params){
        JSONObject jo = new JSONObject();
        try {
            for(int i=0;i<params.length-1;i+=2){
                Object key = params[i];
                Object value = params[i+1];
                jo.appendField(String.valueOf(key),String.valueOf(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jo;
    }

    public static JSONObject getErrorJson(String message){
        return getJson("code","100","message",message);
    }

    public static JSONObject getJson(Map<?,?> m){
        JSONObject jo = new JSONObject();
        for(Map.Entry<?,?> e:m.entrySet()){
            jo.appendField(e.getKey().toString(),e.getValue().toString());
        }
        return jo;
    }
}
