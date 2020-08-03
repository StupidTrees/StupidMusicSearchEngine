package com.stupidtree.sse.crawler;

import com.stupidtree.sse.model.Page;
import com.stupidtree.sse.utils.SqlSessionFactoryUtil;
import org.apache.ibatis.session.SqlSession;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
class SQLPipeline implements Pipeline {


    public SQLPipeline() {

    }

    @Override
    public void process(ResultItems resultitems, Task task) {
        task.getSite().setUserAgent(UserAgentBox.getRandomUserAgent());//更新UA
       try (SqlSession session = SqlSessionFactoryUtil.getSqlSessionFactory().openSession()) {
            Page page = new Page();
            page.setUrl(resultitems.getRequest().getUrl());
            page.setTitle(resultitems.get("title"));
            page.setScore(resultitems.get("score"));
            page.setInfo(resultitems.get("info"));
            page.setDescription(resultitems.get("description"));
            page.setTracklist(resultitems.get("tracklist"));
            page.setComments(resultitems.get("comments"));
            page.setReviews(resultitems.get("reviews"));
            page.setImg(resultitems.get("img"));
            page.setVoted(resultitems.get("voted"));
            session.insert("addPage", page);
            session.commit();
            session.close();
            Crawler.updateProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}