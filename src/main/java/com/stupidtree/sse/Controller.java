package com.stupidtree.sse;

import com.stupidtree.sse.crawler.Crawler;
import com.stupidtree.sse.indexer.Indexer;
import com.stupidtree.sse.model.SearchResult;
import com.stupidtree.sse.searcher.Searcher;
import com.stupidtree.sse.utils.Config;
import com.stupidtree.sse.utils.JsonUtils;
import net.minidev.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@org.springframework.stereotype.Controller
public class Controller {



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
                model.addAttribute("resList", result.getResultItems());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "result";

    }

    @ResponseBody
    @RequestMapping("/config")
    public JSONObject config(@RequestParam(value = "command", defaultValue = "show") String command,
                             @RequestParam(value = "key", defaultValue = "") String key,
                             @RequestParam(value = "val", defaultValue = "") String val) {
        switch (command) {
            case "show":
                return JsonUtils.getJson(Config.getConfigs());
            case "write":
                if (TextUtils.isEmpty(key) || TextUtils.isEmpty(val)) {
                    return JsonUtils.getErrorJson("param invalid");
                } else {
                    try {
                        Config.writeConfig(key, val);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return JsonUtils.getErrorJson(e.getMessage());
                    }
                    return JsonUtils.getJson(Config.getConfigs());
                }

        }
        return JsonUtils.getJson(Config.getConfigs());
    }




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


    private HashMap<String,String> decodeForm(String form){
        HashMap<String,String> res = new HashMap<>();
        for(String s:form.split("&")){
            String[] v = s.split("=");
            res.put(v[0],v[1]);
        }
        return res;
    }
}
