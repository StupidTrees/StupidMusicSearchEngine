package com.stupidtree.sse.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * 配置类
 */
public class Config {
    static HashMap<String,String> properties;//存放配置信息

//    static{ //静态初始化
//        try {
//            init();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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

    public static float getFloatConfig(String key){
        try {
            String val = properties.get(key);
            return Float.parseFloat(val);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * 初始化，装载配置
     * @throws IOException:读文件错误
     */
    public static void init() throws IOException {
        properties = new HashMap<>();
        Properties props = new Properties();
        InputStream in = Config.class.getResourceAsStream("/config.properties");
        props.load(in);
        in.close();
        for (Object key:props.keySet()){
            properties.put(key.toString(),props.getProperty(String.valueOf(key)));
        }
    }

}
