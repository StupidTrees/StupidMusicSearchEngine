package com.stupidtree.sse;

import com.stupidtree.sse.indexer.AnalyzerBox;
import com.stupidtree.sse.utils.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import java.io.IOException;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class SseApplication {

    public static void main(String[] args) {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SpringApplication.run(SseApplication.class, args);
    }


    private static void init() throws IOException {

        Config.init();//初始化配置信息
        AnalyzerBox.init();
    }
}
