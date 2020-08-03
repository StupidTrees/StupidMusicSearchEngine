package com.stupidtree.sse.model;

public class ResultItem {
    String url;
    String img;
    String title;
    String info;

    public ResultItem(String url, String title, String info,String img) {
        this.url = url;
        this.title = title;
        this.info = info;
        this.img = img.replaceAll("https","http");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
