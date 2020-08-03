package com.stupidtree.sse.searcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class AnalyzerBox {
    public static Analyzer getAnalyzer(){
        return new AnsjAnalyzer(AnsjAnalyzer.TYPE.query_ansj);
        //return new IKAnalyzer4Lucene7(false);
    }
}
