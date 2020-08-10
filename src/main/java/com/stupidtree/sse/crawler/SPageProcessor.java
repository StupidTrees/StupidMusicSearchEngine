package com.stupidtree.sse.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.LinkedList;
import java.util.List;


/**
 * 爬虫页面处理类
 */
public class SPageProcessor implements PageProcessor {
    private final Site site = Site.me();


    SPageProcessor(){
        addCookies(site, "gr_user_id=b663e34f-e3ef-48f0-a048-17d4d6543b76; _vwo_uuid_v2=DA176E2741930FB686D4685AB2C491B42|91cad2eb692612afe218a3a11ecaecdc; douban-fav-remind=1; bid=\"piLaPO79DQs\"; viewed=\"12715407_26966195\"; ll=\"118203\"; dbcl2=\"159679748:jmL/U2ImSMM\"; push_noty_num=0; push_doumail_num=0; __utmv=30149280.15967; _ga=GA1.2.35652654.1547173704; __utmz=30149280.1596019230.24.15.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; ck=oJyo; __utmc=30149280; _pk_ref.100001.afe6=%5B%22%22%2C%22%22%2C1596185544%2C%22http%3A%2F%2Flocalhost%3A8080%2Fsearch%3Ftext%3D%2B%25E8%25B0%25AD%25E7%25BB%25B4%25E7%25BB%25B4%22%5D; _pk_ses.100001.afe6=*; __utma=30149280.35652654.1547173704.1596177490.1596185545.35; _pk_id.100001.afe6=0559dbccb40ffe8a.1595682532.26.1596186185.1596183636.; __utmt=1; __utmb=30149280.8.10.1596185545");
        site.setTimeOut(10000);
        site.addHeader("Host", "music.douban.com");
        site.addHeader("Sec-Fetch-User", "?1");
        site.addHeader("Upgrade-Insecure-Requests", "1");
        site.setDomain("https://music.douban.com/")
                .setSleepTime(5)
                .setRetrySleepTime(6000)
                .setUseGzip(true)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36 Edg/84.0.522.48");

    }

    //将html中的信息解析，半结构化保存到page对象中，交给pipeline处理
    @Override
    public void process(Page page) {
        Document document = page.getHtml().getDocument();
        String score = extractText(document.getElementsByClass("ll rating_num").first());
        String title = page.getHtml().xpath("//title/text()").get().replace(" (豆瓣)", "");
        String info = extractText(document.getElementById("info"));
        String description = extractText(document.getElementById("link-report"));
        String trackList = extractText(document.getElementsByClass("track-list").first());
        StringBuilder comments = new StringBuilder();
        for (Element comment : document.getElementById("comment-list-wrapper").getElementsByClass("comment-item")) {
            String user = "";
            try {
                user = extractText(comment.getElementsByClass("comment-info").first().getElementsByTag("a").first());
            } catch (Exception ignored) {

            }
            String content = extractText(comment.getElementsByClass("comment-content").first());
            comments.append(user).append(":").append(content).append("\n");
        }
        StringBuilder reviews = new StringBuilder();
        for (Element review : document.getElementsByClass("main review-item")) {
            String id = review.id();
            String user = extractText(review.getElementsByClass("name").first());
            String head = extractText(review.getElementsByClass("main-bd").first().getElementsByTag("a").first());
            String content = extractText(review.getElementById("review_" + id + "_short"));
            reviews.append(user).append(":").append("《").append(head).append("》：").append(content).append("\n");
        }
        String img = document.getElementById("mainpic").getElementsByTag("img").first().attr("src");
        int voted = -1;
        try {
            voted = Integer.parseInt(document.getElementsByClass("rating_sum").first().getElementsByTag("span").text());
        } catch (Exception ignored) {
        }
        page.putField("title", title);
        page.putField("score", score);
        page.putField("info", info);
        page.putField("description", description);
        page.putField("tracklist", trackList);
        page.putField("comments", comments.toString());
        page.putField("reviews", reviews.toString());
        page.putField("img", img);
        page.putField("voted", voted);
        List<String> links = page.getHtml().links().regex("https://music.douban.com/subject/\\d+/").all();
        page.addTargetRequests(links);

    }


    //抽取某tag下的所有文本，包含其子tag
    private String extractText(Element e) {
        if (e == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        LinkedList<Element> queue = new LinkedList<>();
        queue.add(e);
        while (!queue.isEmpty()) {
            Element c = queue.remove(0);
            sb.append(c.ownText());
            queue.addAll(c.children());
        }
        return sb.toString();
    }

    private static void addCookies(Site site, String cookies) {
        for (String cookie : cookies.split(";")) {
            try {
                String key = cookie.split("=")[0].replaceAll(" ", "").replaceAll("\"", "");
                String val = cookie.split("=")[1].replaceAll(" ", "").replaceAll("\"", "");
                site.addCookie(key, val);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
