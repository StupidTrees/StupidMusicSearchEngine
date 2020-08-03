package com.stupidtree.sse.searcher;

import com.stupidtree.sse.model.ResultItem;
import com.stupidtree.sse.model.SearchResult;
import com.stupidtree.sse.utils.Config;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Searcher {
    private static final int HIGHLIGHT_MIN_DISTANCE = 10;
    private static final int CLIP_MAX_LENGTH = 40;

    public static SearchResult search(String queryString, int page) throws Exception {
        page = Math.max(page,1);
        int pageSize = Config.getIntegerConfig("result_num");
        //分词器
        Analyzer analyzer = AnalyzerBox.getAnalyzer();

        // 创建IndexSearcher
        // 指定索引库的地址
        Path indexFile = Paths.get(Config.getStringConfig("index_path"));
        Directory directory = FSDirectory.open(indexFile);
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(reader);


        Query query =  QueryFactory.getQuery(queryString);

        // 通过searcher来搜索索引库
        // 第二个参数：指定需要显示的顶部记录的N条
        TopDocs topDocs = indexSearcher.search(query,page*pageSize);

        // 高亮工具，格式化器,算分器
        QueryScorer scorer = new QueryScorer(query);
        Formatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
        Highlighter highlighter = new Highlighter(formatter, scorer);

        // 根据查询条件匹配出的记录
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        List<ResultItem> results = new ArrayList<>();
        for(int i=pageSize*(page-1),j=0;i<scoreDocs.length&&j<pageSize;i++,j++){
            ScoreDoc scoreDoc = scoreDocs[i];
            // 获取文档的ID
            int docId = scoreDoc.doc;
            // 通过ID获取文档
            Document doc = indexSearcher.doc(docId);
            String titleH = nullOrRaw(highlighter.getBestFragment(analyzer,"title",doc.get("title")),doc.get("title"));
            String snippet = getSnippet(doc,query,analyzer);
            results.add(new ResultItem(doc.get("url"),titleH,snippet,doc.get("img")));
        }
        // 关闭资源
        reader.close();
        return new SearchResult(results,topDocs.totalHits.value,page);
    }


    private static String getSnippet(Document document,Query query,Analyzer analyzer) throws IOException, InvalidTokenOffsetsException {
        // 格式化器,算分器
        Formatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
        QueryScorer scorer = new QueryScorer(query);
        // 准备高亮工具
        Highlighter highlighter = new Highlighter(formatter, scorer);
        StringBuilder snippet = new StringBuilder();
        for(IndexableField f:document.getFields()){
            if("title".equals(f.name())) {
                continue;
            }

            highlighter.setTextFragmenter(new SimpleFragmenter(f.stringValue().length()));

            TokenStream tokenStream = analyzer.tokenStream(f.name(), new StringReader(
                    f.stringValue()));
            String hi = highlighter.getBestFragment(tokenStream, f.stringValue());
            //String hi = highlighter.getBestFragment(analyzer,f.name(),f.stringValue());
            if(hi==null){
                continue;
            }
           // System.out.println(hi);
            snippet.append(getClip(hi,f.stringValue())).append("...");

        }
        return snippet.toString();
    }
    private static String nullOrRaw(String text,String other){
        return text==null?other:text;
    }
    private static String getClip(String raw,String clean){

        List<HighlightBlock> highlightBlocks = clipsOf(raw,"<em>","</em>");
        List<HighlightBlock> newHBs = new ArrayList<>();
        //将较近距的高亮点聚合
        for(HighlightBlock hb:highlightBlocks){
            if(newHBs.size()==0||hb.start-newHBs.get(newHBs.size()-1).end>HIGHLIGHT_MIN_DISTANCE){
                newHBs.add(hb);
            }else {
                newHBs.get(newHBs.size()-1).updateContent(raw,hb.end);
            }
        }
       StringBuilder clip = new StringBuilder();
        for(HighlightBlock hb:newHBs){
//            System.out.println(hb.getContent());

            if (hb.length() <= CLIP_MAX_LENGTH) {
                hb.fitLength(CLIP_MAX_LENGTH, clean);
            }
//            System.out.println("==>"+hb.getContent());
            clip.append(hb.getContent());
        }
        return clip.toString();
    }
    private static List<Integer> indexesOf(String s,String sub){
        Matcher m = Pattern.compile("(?=("+sub+"))").matcher(s);
        List<Integer> pos = new ArrayList<Integer>();
        while (m.find())
        {
            pos.add(m.start());
        }
        return pos;
    }
    private static List<HighlightBlock> clipsOf(String s,String startTag,String endTag){
        List<Integer> indexesB = indexesOf(s,startTag);
        List<Integer> indexesE = indexesOf(s,endTag);
        List<HighlightBlock> highlightBlocks = new ArrayList<>();
        int abandon = 0;
        for(int i=0;i<indexesB.size();i++){
            int indexInClean = indexesB.get(i)-abandon;
            HighlightBlock hb = new HighlightBlock(s,indexesB.get(i), indexesE.get(i)+endTag.length(),indexInClean);
            highlightBlocks.add(hb);
            abandon+=startTag.length()+endTag.length();
        }
        return highlightBlocks;
    }

    static class HighlightBlock{
        int start;
        int end;
        int indexInClean;
        String content;

        public HighlightBlock(String raw,int start, int end,int indexInClean) {
            this.start = start;
            this.end = end;
            content = raw.substring(start,end);
            this.indexInClean = indexInClean;
        }

        int length(){
            return content.length();
        }

        String getContent(){
           return content;
        }
        void updateContent(String raw,int newEnd){
            end = newEnd;
            content = raw.substring(start,end);
        }
        void fitLength(int total,String clean){
            int expand = (total-length())/2+(total-length())%2;
            int cleanLength = deTag(content).length();
            if(cleanLength>=clean.length()) return;
            indexInClean = Math.min(indexInClean,clean.length()-1-cleanLength);
            indexInClean = Math.max(0,indexInClean);
            String before = clean.substring(Math.max(0,indexInClean-expand),indexInClean);
            String after = clean.substring(indexInClean+cleanLength,Math.min(clean.length()-1,indexInClean+cleanLength+expand));
            content = before+content+after;
        }

        String deTag(String s){
            return s.replaceAll("<em>","").replaceAll("</em>","");
        }
    }

}
