package com.stupidtree.sse.model;

public class Page {
    int id = 0;
    int voted = 0;
    String url;
    String title;
    String score;
    String info;
    String description;
    String tracklist;
    String comments;
    String reviews;
    String img;
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
