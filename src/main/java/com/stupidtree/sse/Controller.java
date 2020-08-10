package com.stupidtree.sse;

import com.stupidtree.sse.crawler.Crawler;
import com.stupidtree.sse.indexer.Indexer;
import com.stupidtree.sse.model.SearchResult;
import com.stupidtree.sse.searcher.Searcher;
import net.minidev.json.JSONObject;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Springboot控制对象：映射http请求
 */
@org.springframework.stereotype.Controller
public class Controller {


    /**
     * 进行检索
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam("text") String text,
                         @RequestParam(value = "page",defaultValue = "1") int page,
                         Model model) {
        try {
            if (Indexer.indexExists()) {
                SearchResult result = Searcher.search(text,page);
                model.addAttribute("total", result.getTotalNum());
                model.addAttribute("pageList",result.getPageNumbers());
                model.addAttribute("totalPages",result.getTotalPages());
                model.addAttribute("query", text);
                model.addAttribute("page",result.getPage());
                model.addAttribute("time",result.getResponseTime());
                model.addAttribute("resList", result.getResultItems());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "result";

    }


    /**
     * 控制台
     */
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(@RequestParam(value = "command", defaultValue = "state") String command,
                            Model model){
        switch (command) {
            case "crawler-start":
                Crawler.Start();
                break;
            case "crawler-stop":
                Crawler.Stop();
                break;
            case "crawler-finish":
                Crawler.finish();
                break;
            case "indexer-start":
                Indexer.start();
                break;
            case "indexer-delete":
                Indexer.deleteIndex();
                break;
            case "state":
        }
        JSONObject state = Crawler.showState();
        JSONObject state2 = Indexer.showState();
        model.addAttribute("crawler_status",state.get("status"));
        model.addAttribute("crawler_threadAlive",state.get("threadAlive"));
        model.addAttribute("crawler_pageCount",state.get("pageCount"));
        model.addAttribute("crawler_downloaded",state.get("downloaded"));
        model.addAttribute("indexer_status",state2.get("status"));
        model.addAttribute("indexer_exist",state2.get("indexing-exist"));
        model.addAttribute("indexer_thread",state2.get("thread"));
        model.addAttribute("indexer_message",state2.get("message"));
        return "dashboard";

    }


}
