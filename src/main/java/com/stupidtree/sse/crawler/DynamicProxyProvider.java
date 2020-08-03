package com.stupidtree.sse.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicProxyProvider implements ProxyProvider {
    private final List<Proxy> proxies;
    private final AtomicInteger pointer;
    boolean changeIP = true;
    public DynamicProxyProvider(float minutes) {
        this(new ArrayList<>(), new AtomicInteger(-1));
        int mill = (int) (minutes*60*1000);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("定时时间到，开始更换IP");
                changeIP = true;
            }
        },mill,mill);
    }

    private DynamicProxyProvider(List<Proxy> proxies, AtomicInteger pointer) {
        this.proxies = proxies;
        this.pointer = pointer;
    }

/*
    public static DynamicProxyProvider from(Proxy... proxies) {
        ArrayList<Proxy> proxiesTemp = new ArrayList<>(proxies.length);
        int var3 = proxies.length;

        proxiesTemp.addAll(Arrays.asList(proxies).subList(0, var3));

        return new DynamicProxyProvider(Collections.unmodifiableList(proxiesTemp));
    }
*/

    @Override
    public void returnProxy(Proxy proxy, Page page, Task task) {
    }

    @Override
    public Proxy getProxy(Task task) {
        if (changeIP){
            changeIP = false;
            refreshIPs();
        }
        if(proxies==null||proxies.size()<1) {
            return null;
        }
        return this.proxies.get(this.incrForLoop());
    }

    private int incrForLoop() {
        int p = this.pointer.incrementAndGet();
        int size = this.proxies.size();
        if (p < size) {
            return p;
        } else {
            while(!this.pointer.compareAndSet(p, p % size)) {
                p = this.pointer.get();
            }

            return p % size;
        }
    }


    private void refreshIPs(){
        proxies.clear();
        try {
            Connection c =
                    Jsoup.connect("http://dps.kdlapi.com/api/getdps")
                            .data("orderid","949635147994496")
                            .data("num","10")
                            .data("format","json")
                            .ignoreContentType(true)
                            .data("sep","1").method(Connection.Method.GET);
            c.execute();
            JSONObject jo = JSONObject.parseObject(c.response().body());
            int code = jo.getInteger("code");
            if(code==0){ //还有可用的
                JSONArray ja = jo.getJSONObject("data").getJSONArray("proxy_list");
                System.out.println("===代理池已更新===");
                for(int i=0;i<ja.size();i++){
                    String str = ja.getString(i);
                    proxies.add(new Proxy(str.split(":")[0],Integer.parseInt(str.split(":")[1])));
                    System.out.println(str);
                }

            }else{
                System.out.println("===代理获取失败:code="+code+",msg="+jo.getString("msg")+"===");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
