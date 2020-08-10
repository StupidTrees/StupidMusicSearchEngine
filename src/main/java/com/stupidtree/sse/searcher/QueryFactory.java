package com.stupidtree.sse.searcher;

import com.stupidtree.sse.indexer.AnalyzerBox;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.http.util.TextUtils;
import org.apache.lucene.search.*;

import java.util.List;


/**
 * Query构建器
 */
public class QueryFactory {
    //所有需要查询的域
    static String[] fields = new String[]{"title", "comments", "reviews", "info", "tracklist", "description"};
   // static float[] weights = new float[]{1.5f, 0.7f, 0.7f, 1f, 0.3f, 0.4f};
    //分配给这些域的初始权重
    static float[] weights = new float[]{5.1f, 1.65f, 2f, 2.5f, 0.9f, 1.2f};


    /**
     * 获取一个Lucene查询
     * @param query 查询语句
     * @return lucene查询对象
     */
    public static Query getQuery(String query) {
        BooleanQuery.Builder bf = new BooleanQuery.Builder();
        String[] causes = query.split(" "); //空格默认表示“与”关系
        for(String text:causes){
            if(TextUtils.isEmpty(text)) {
                continue;
            }
            List<Term> qTerms = ToAnalysis.parse(text, AnalyzerBox.getDefaultForest()).getTerms();
            System.out.println(qTerms);
            BooleanQuery.Builder bb = new BooleanQuery.Builder();
            for (Term term : qTerms) {
                BooleanQuery.Builder bbT = new BooleanQuery.Builder();
                for (int i = 0; i < fields.length; i++) {
                    TermQuery q = new TermQuery(new org.apache.lucene.index.Term(fields[i], term.getName()));
                    float fct = getImportanceFactor(term);
                    BoostQuery bq = new BoostQuery(q, weights[i]*fct);
                    bbT.add(bq, BooleanClause.Occur.SHOULD);
                }
                bb.add(bbT.build(), BooleanClause.Occur.SHOULD);
            }
            if(causes.length==1){
                return bb.build();
            }
            bf.add(bb.build(), BooleanClause.Occur.MUST);
        }
        return bf.build();

    }


    //根据Term的词性调整boost
    private static float getImportanceFactor(Term term) {
        String tag = term.getNatureStr();
        String content = term.getName();
        if("歌".equals(content) || "歌曲".equals(content)||"音乐".equals(content)) { //话题常用词权重降低
            return 0.01f;
        }
        if (tag.startsWith("u")) {
            return 0.05f;//助词
        } else if (tag.startsWith("y")) {
            return 0.05f;//语气词
        } else if (tag.startsWith("c")) {
            return 0.05f;//连词
        } else if (tag.startsWith("p")) {
            return 0.05f;//介词
        } else if("nr".equals(tag)){
            return 3f;//人名boost
        }
        if(content.length()<2){
            return 0.06f;//单个字的权重降低
        }else {
            float fact;
            if(!"n".equals(tag)){
                fact = 0.51f;
            }else{
                fact = 1.2f;
                if(content.contains("曲")){ //协奏曲、狂想曲、进行曲等
                    fact*=0.2;
                }
            }
            return content.length()*fact;//长词组的权重加大
        }

    }



//    private static Query getQuery(String queryText, Analyzer analyzer) throws ParseException {
//
//        HashMap<String, Float> boosts = new HashMap<>();
//        for (int i = 0; i < fields.length; i++) {
//            boosts.put(fields[i], weights[i]);
//        }
//        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);
//        parser.setDefaultOperator(QueryParser.Operator.AND);
//        Query query = parser.parse(QueryParserBase.escape(queryText)); // 先转义
////        TermQuery tq1 = new TermQuery(new Term("title",queryText));
////        TermQuery tq2 = new TermQuery(new Term("info",queryText));
////        TermQuery tq3 = new TermQuery(new Term("tracklist",queryText));
////        TermQuery tq4 = new TermQuery(new Term("comments",queryText));
////        TermQuery tq5 = new TermQuery(new Term("reviews",queryText));
////
////        BooleanQuery.Builder builder = new BooleanQuery.Builder();
////        builder.add(new BooleanClause(new BoostQuery(query,90), BooleanClause.Occur.MUST));
////        builder.add(new BooleanClause(new BoostQuery(tq1,100), BooleanClause.Occur.SHOULD));
////        builder.add(new BooleanClause(new BoostQuery(tq2,60), BooleanClause.Occur.SHOULD));
////        builder.add(new BooleanClause(new BoostQuery(tq3,60), BooleanClause.Occur.SHOULD));
////        builder.add(new BooleanClause(new BoostQuery(tq4,80), BooleanClause.Occur.SHOULD));
////        builder.add(new BooleanClause(new BoostQuery(tq5,80), BooleanClause.Occur.SHOULD));
//
//        return query;
//    }

}
