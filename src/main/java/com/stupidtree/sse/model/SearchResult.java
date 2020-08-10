package com.stupidtree.sse.model;

import com.stupidtree.sse.utils.Config;
import com.stupidtree.sse.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果的一页
 */
public class SearchResult {
    List<ResultItem> resultItems;
    long totalNum;
    int page;
    int totalPages;
    int pageSize;
    float responseTime;

    public SearchResult(List<ResultItem> resultItems, long totalNum, int page, float responseTime) {
        this.resultItems = resultItems;
        this.totalNum = totalNum;
        this.page= page;
        this.responseTime = responseTime;
        pageSize = Config.getIntegerConfig("result_num");
        totalPages = (int) (totalNum/pageSize );
    }

    public float getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(float responseTime) {
        this.responseTime = responseTime;
    }

    public List<ResultItem> getResultItems() {
        return resultItems;
    }

    public long getTotalNum() {
        return totalNum;
    }
    public List<Integer> getPageNumbers(){
        List<Integer> r = new ArrayList<>();
        if(page==1){
            for(int j=0;j*pageSize<totalNum&&j<7;j++){
                r.add(page+j);
            }
        }else{
            for(int j=0;(j+page-1)*pageSize<totalNum&&j<7;j++){
                r.add(page+j-1);
            }
        }
        return r;
    }

    public int getTotalPages(){
        return totalPages;
    }

    public int getPage() {
        return page;
    }
}
