package com.stupidtree.sse.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

public class Config {
    static HashMap<String,String> properties;

    static{
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getStringConfig(String key){
        return properties.get(key);
    }

    public static int getIntegerConfig(String key){
        try {
            String val = properties.get(key);
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static HashMap<String,String> getConfigs(){
        return properties;
    }

    private static void init() throws IOException {
        properties = new HashMap<>();
        Properties props = new Properties();
        InputStream in = Config.class.getResourceAsStream("/config.properties");
        props.load(in);
        in.close();
        for (Object key:props.keySet()){
            properties.put(key.toString(),props.getProperty(String.valueOf(key)));
        }
    }

    public static void writeConfig(String key,String val) throws IOException {
        Properties props = new Properties();
        InputStream in = Config.class.getResourceAsStream("/config.properties");
        props.load(in);
        OutputStream output = new FileOutputStream("/config.properties");
        props.setProperty(key, val); // 修改或新增属性键值对
        props.store(output, "modify "+key+" value"); // store(OutputStream output, String comment)将修改结果写入输出流
        output.close();
        for (Object k:props.keySet()){
            properties.put(k.toString(),props.getProperty(String.valueOf(k)));
        }
    }
}
