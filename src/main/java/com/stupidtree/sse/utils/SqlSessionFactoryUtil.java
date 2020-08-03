package com.stupidtree.sse.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class SqlSessionFactoryUtil {

    private static SqlSessionFactory sessionFactory; //会话工厂设置值

    static {

        //初始化 加载环境 创建会话工厂 获取会话
        try {
            //1.加载mybatis的运行环境 io流的方式去读取全局配置文件
            InputStream iStream = Resources.getResourceAsStream("sqlMapConfig.xml");
            //2.创建会话工厂
            sessionFactory=new SqlSessionFactoryBuilder().build(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static SqlSessionFactory getSqlSessionFactory() {
        if(sessionFactory==null){
            try {
                InputStream iStream = Resources.getResourceAsStream("sqlMapConfig.xml");
                sessionFactory=new SqlSessionFactoryBuilder().build(iStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}