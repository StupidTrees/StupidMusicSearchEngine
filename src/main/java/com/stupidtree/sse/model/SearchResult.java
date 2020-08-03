package com.stupidtree.sse.model;

import com.stupidtree.sse.utils.Config;
import com.stupidtree.sse.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
    List<ResultItem> resultItems;
    long totalNum;
    int page;
    int totalPages;
    int pageSize;

    public SearchResult(List<ResultItem> resultItems, long totalNum, int page) {
        this.resultItems = resultItems;
        this.totalNum = totalNum;
        this.page= page;
        pageSize = Config.getIntegerConfig("result_num");
        totalPages = (int) (totalNum/pageSize );
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
