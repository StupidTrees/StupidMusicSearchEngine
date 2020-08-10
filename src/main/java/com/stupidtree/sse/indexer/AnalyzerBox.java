package com.stupidtree.sse.indexer;

import com.stupidtree.sse.searcher.QueryFactory;
import org.ansj.domain.Result;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;

/**
 * 分词工具管理器
 */
public class AnalyzerBox {

    public static Analyzer getAnalyzer(){
        return new AnsjAnalyzer(AnsjAnalyzer.TYPE.query_ansj,"userLibrary");
    }

    /**
     * 分词初始化
     * 需要在服务启动时进行，否则检索速度将被拖慢
     */
    public static void init(){
        try {
            Forest forest = Library.makeForest(QueryFactory.class.getResourceAsStream("/library/userLibrary.dic"));//加载字典文件
            Result r = ToAnalysis.parse("分词初始化",forest);
            System.out.println(r.getTerms());
            DicLibrary.put("userLibrary","/library/userLibrary.dic",forest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Forest getDefaultForest(){
        return DicLibrary.get("userLibrary");
    }


}
