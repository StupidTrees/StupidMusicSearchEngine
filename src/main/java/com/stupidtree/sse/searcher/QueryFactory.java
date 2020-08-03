package com.stupidtree.sse.searcher;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.AnsjReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.*;
import org.apache.lucene.search.vectorhighlight.FieldQuery;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryFactory {
    static String[] fields = new String[]{"title", "comments", "reviews", "info", "tracklist", "description"};
    static float[] weights = new float[]{1f, 0.5f, 0.5f, 0.6f, 0.1f, 0.1f};
    static ToAnalysis analysis = new ToAnalysis();


    public static Query getQuery(String text) {
        analysis.resetContent(new StringReader(text));

        List<Term> qTerms = new ArrayList<>();
        try {
            qTerms = analysis.parse().getTerms();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        System.out.println(bb.build().toString());
        return bb.build();
    }


    private static float getImportanceFactor(Term term) {
        String tag = term.getNatureStr();
        if (tag.startsWith("u")) {
            return 0.1f;//助词
        } else if (tag.startsWith("y")) {
            return 0.1f;//语气词
        } else if (tag.startsWith("c")) {
            return 0.1f;//连词
        } else if (tag.startsWith("p")) {
            return 0.1f;//介词
        }
        if(term.getName().length()<2){
            return 0.5f;//单个字的权重降低
        }
        return 1f;

    }

    private static Query getQuery(String queryText, Analyzer analyzer) throws ParseException {

        HashMap<String, Float> boosts = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            boosts.put(fields[i], weights[i]);
        }
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);
        parser.setDefaultOperator(QueryParser.Operator.AND);
        Query query = parser.parse(QueryParserBase.escape(queryText)); // 先转义
//        TermQuery tq1 = new TermQuery(new Term("title",queryText));
//        TermQuery tq2 = new TermQuery(new Term("info",queryText));
//        TermQuery tq3 = new TermQuery(new Term("tracklist",queryText));
//        TermQuery tq4 = new TermQuery(new Term("comments",queryText));
//        TermQuery tq5 = new TermQuery(new Term("reviews",queryText));
//
//        BooleanQuery.Builder builder = new BooleanQuery.Builder();
//        builder.add(new BooleanClause(new BoostQuery(query,90), BooleanClause.Occur.MUST));
//        builder.add(new BooleanClause(new BoostQuery(tq1,100), BooleanClause.Occur.SHOULD));
//        builder.add(new BooleanClause(new BoostQuery(tq2,60), BooleanClause.Occur.SHOULD));
//        builder.add(new BooleanClause(new BoostQuery(tq3,60), BooleanClause.Occur.SHOULD));
//        builder.add(new BooleanClause(new BoostQuery(tq4,80), BooleanClause.Occur.SHOULD));
//        builder.add(new BooleanClause(new BoostQuery(tq5,80), BooleanClause.Occur.SHOULD));

        return query;
    }


    public static void main(String[] args) {
        getQuery("一杯敬朝阳的话");
    }
}
