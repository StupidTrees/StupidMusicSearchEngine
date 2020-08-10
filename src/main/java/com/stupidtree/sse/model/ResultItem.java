package com.stupidtree.sse.model;

import org.apache.lucene.document.Document;


/**
 * 检索结果的一项
 */
public class ResultItem {
    String url;
    String img;
    String title;
    String info;
    String score;
    int voted;
    float sort_score;

    public ResultItem(Document doc,float sort_score,String title,String snippet) {
        this.url = doc.get("url");
        this.title = title;
        this.info = snippet;
        this.img = doc.get("img");
        this.score = doc.get("score");
        this.sort_score = sort_score;
        try {
            voted = Integer.parseInt(doc.get("voted"));
        } catch (Exception e) {
            voted = -1;
            e.printStackTrace();
        }
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

    public int getVoted() {
        return voted;
    }

    public void setVoted(int voted) {
        this.voted = voted;
    }

    public float getSort_score() {
        return sort_score;
    }

    public void setSort_score(float sort_score) {
        this.sort_score = sort_score;
    }
}
