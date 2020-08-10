package com.stupidtree.sse.model;

/**
 * 一个豆瓣音乐页面
 */
public class Page {
    int id = 0; //在数据库中的id
    int voted = 0; //评分人数
    String url; //网页链接
    String title; //标题
    String score; //豆瓣评分
    String info; //基本信息
    String description; //描述
    String tracklist; //曲目表
    String comments; //评论
    String reviews; //乐评
    String img; //封面url
    public Page(){
        super();
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReviews() {
        return reviews;
    }

    public String getTracklist() {
        return tracklist;
    }

    public void setTracklist(String tracklist) {
        this.tracklist = tracklist;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getVoted() {
        return voted;
    }

    public void setVoted(int voted) {
        this.voted = voted;
    }
}
