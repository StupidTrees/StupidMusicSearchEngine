package com.stupidtree.sse.crawler;

import com.stupidtree.sse.model.Page;
import com.stupidtree.sse.utils.SqlSessionFactoryUtil;
import org.apache.ibatis.session.SqlSession;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 爬虫过程的pipeline，用于将下载的页面保存到mysql
 */
class SPipeline implements Pipeline {


    public SPipeline() {

    }

    @Override
    public void process(ResultItems resultitems, Task task) {
        task.getSite().setUserAgent(UserAgentBox.getRandomUserAgent());//更新UA
       try (SqlSession session = SqlSessionFactoryUtil.getSqlSessionFactory().openSession()) {
           //构建page对象
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
            //插入到mysql数据库中
            session.insert("addPage", page);
            session.commit();
            session.close();
            Crawler.updateProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}