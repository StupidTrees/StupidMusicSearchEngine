package com.stupidtree.sse.indexer;


import com.stupidtree.sse.model.Page;
import com.stupidtree.sse.utils.Config;
import com.stupidtree.sse.utils.JsonUtils;
import net.minidev.json.JSONObject;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * 索引控制类
 */
public class Indexer {
    static Thread indexer;
    static String status = "not-started";
    static String message = "";
    private static String fillNull(String s) {
        return s == null ? "" : s;
    }


    /**
     * 开始构建索引
     */
    public static void start()  {
        if("running".equals(status)) {
            return;
        }

        indexer = new Thread(() -> {
            try {
                status = "running";
                // 创建分词器
                Analyzer analyzer = AnalyzerBox.getAnalyzer();
                // 创建IndexWriter
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                // 指定索引库的地址
                Path indexFile = Paths.get(Config.getStringConfig("index_path"));
                Directory directory = FSDirectory.open(indexFile);
                IndexWriter indexWriter = new IndexWriter(directory, config);

                // 采集数据
                InputStream in = Resources.getResourceAsStream("sqlMapConfig.xml");
                SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
                SqlSession session = factory.openSession();
                //逐行读入、写进index
                session.select("Page.getPagesForward", resultContext -> {
                    Page page = (Page) resultContext.getResultObject();
                    try {
                        indexWriter.addDocument(createDocument(page));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                // 关闭indexWriter
                indexWriter.close();
                status = "done";
            } catch (IOException e) {
                e.printStackTrace();
                status = "error";
                message = e.getMessage();
            }
        });
        indexer.start();
    }


    /**
     * 删除索引
     */
    public static void deleteIndex() {
        if(!"running".equals(status)){
            delete(Config.getStringConfig("index_path"));
        }
    }


    /**
     * @return 索引是否存在
     */
    public static boolean indexExists(){
        File file = new File(Config.getStringConfig("index_path"));
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return true;
        }
        File[] files = file.listFiles();
        return files!=null&&files.length>0;
    }


    /**
     * 构建索引需要的Document对象
     */
    private static Document createDocument(Page page){
        Document document = new Document();

        FieldType type0=new FieldType();
        type0.setStored(true);
        type0.setTokenized(true);//设置分词
        type0.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);

        FieldType type1=new FieldType();
        type1.setStored(true);
        type1.setTokenized(true);//设置分词
        type1.setOmitNorms(false);//长度正则
        type1.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);

        FieldType type2=new FieldType();
        type2.setStored(true);
        type2.setTokenized(false);//设置分词
        type2.setIndexOptions(IndexOptions.NONE);//不进行Index

        Field url = new Field("url", fillNull(page.getUrl()),type2);
        Field img = new  Field("img", fillNull(page.getImg()),type2);
        Field score = new Field("score", fillNull(page.getScore()), type2);
        Field voted = new Field("voted",fillNull(page.getVoted()+""),type2);

        Field info = new Field("info", fillNull(page.getInfo()), type1);
        Field description = new Field("description", fillNull(page.getDescription()), type1);
        Field tracklist = new Field("tracklist", fillNull(page.getTracklist()), type1);


        Field title = new Field("title", fillNull(page.getTitle()), type0);
        Field comments = new Field("comments", fillNull(page.getComments()), type0);
        Field reviews = new  Field("reviews", fillNull(page.getReviews()), type0);//不进行正则，越多说明越important


        document.add(url);
        document.add(title);
        document.add(score);
        document.add(info);
        document.add(description);
        document.add(tracklist);
        document.add(comments);
        document.add(reviews);
        document.add(img);
        document.add(voted);
        return document;
    }


    /**
     * @return 索引状态
     */
    public static JSONObject showState(){
        return JsonUtils.getJson("indexing-exist",indexExists(),"status",status,"message",message,"thread",indexer==null?"null":indexer.getState().name());
    }

    private static boolean delete(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    if (!f.delete()) {
                        return false;
                    }
                } else {
                    if (!delete(f.getAbsolutePath())) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

}
