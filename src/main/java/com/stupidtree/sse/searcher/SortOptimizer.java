package com.stupidtree.sse.searcher;

import com.stupidtree.sse.model.ResultItem;
import com.stupidtree.sse.model.SearchResult;
import com.stupidtree.sse.utils.Config;

import java.util.List;

/**
 * 检索排序优化器
 */
public class SortOptimizer {
//    private static final float lambda = 2.4f;

    /**
     * 优化搜索结果
     * @param searchResult:搜索结果对象
     * @return 优化后的搜索结果
     */
    public static SearchResult optimize(SearchResult searchResult){
        float lambda = Config.getFloatConfig("sort_voted_lambda"); //控制公式里的系数lambda
        List<ResultItem> results = searchResult.getResultItems();
        float totalScore = getTotalScore(results);
        float totalVoted = getTotalVoted(results);
        for(ResultItem resultItem:results){
            float votedWeight = resultItem.getVoted()/totalVoted;
            float scoreWeight = resultItem.getSort_score()/totalScore;
            float boost = scoreWeight*votedWeight*results.size()*lambda;
           // System.out.println(resultItem.getTitle()+resultItem.getSort_score()+":"+boost);
            resultItem.setSort_score(resultItem.getSort_score()+boost);
        }
        results.sort((o1, o2) -> (int) (100 *(o2.getSort_score() - o1.getSort_score())));
        return searchResult;
    }


    private static float getTotalScore(List<ResultItem> resultItems){
        float res = 0;
        for(ResultItem r:resultItems){
            res+=r.getSort_score();
        }
        return res;
    }

    private static float getTotalVoted(List<ResultItem> resultItems){
        float res = 0;
        for(ResultItem r:resultItems){
            res+=r.getVoted();
        }
        return res;
    }
}
