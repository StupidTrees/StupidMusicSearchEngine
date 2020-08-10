package com.stupidtree.sse.crawler;

import com.stupidtree.sse.utils.JsonUtils;
import net.minidev.json.JSONObject;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

/**
 * 爬虫类
 */
public class Crawler {
    private static Spider spider;
    private static int progress = 0; //已下载页面数


    /**
     * 爬虫开始
     */
    public static void Start() {
        if (spider != null) {
            if (spider.getStatus() == Spider.Status.Stopped) {
                spider.start();
            }
        }else{
            spider = createNewSpider();
            spider.start();
        }
    }


    /**
     * 爬虫暂停
     */
    public static void Stop() {
        if (spider != null) {
            spider.stop();
        }
    }

    /**
     * 爬虫结束
     */
    public static void finish() {
        if (spider != null) {
            spider.stop();
            spider.close();
        }
        spider = null;
    }


    //创建新的爬虫任务
    private static Spider createNewSpider(){
        SPageProcessor pageProcessor = new SPageProcessor();
        HttpClientDownloader downloader = new HttpClientDownloader();
        downloader.setProxyProvider(new DynamicProxyProvider(3.5f));
        //初始页面：选热门的
        return Spider.create(pageProcessor)
                .addUrl("https://music.douban.com/subject/26966195") //梦龙
                .addUrl("https://music.douban.com/subject/27140918/") //消愁
                .addUrl("https://music.douban.com/subject/26206668/") //BlankSpace
                .addUrl("https://music.douban.com/subject/26940770/")//光年之外
                .addUrl("https://music.douban.com/subject/26030690/")//南山南
                .addUrl("https://music.douban.com/subject/34466411/")//bad guy
                .addUrl("https://music.douban.com/subject/2309868/")//天堂
                .addUrl("https://music.douban.com/subject/33439600/")//麻雀
                .addUrl("https://music.douban.com/subject/25900422/")//小苹果
                .addUrl("https://music.douban.com/subject/35093585/")//Mojito
                .setDownloader(downloader)
                .thread(64)
                .addPipeline(new SPipeline());
    }

    /**
     * 输出爬虫状态
     * @return 爬虫状态JSON
     */
    public static JSONObject showState() {
        if (spider != null) {
            return JsonUtils.getJson("pageCount", spider.getPageCount(),
                    "threadAlive", spider.getThreadAlive(),
                    "status", spider.getStatus().name(),
                    "downloaded", progress);
        } else {
            JSONObject jo = JsonUtils.getErrorJson("spider is null");
            jo.put("status", "spider is null");
            return jo;
        }
    }



    public static void updateProgress() {
        progress++;
    }

}