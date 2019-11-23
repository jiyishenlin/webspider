package com.webspider.tool;

import java.io.IOException;

public class WebBookHelper {

    
    public static void getContent() throws IOException, InterruptedException {
    	GetContent crawler=new GetContent("https://www.biquge.info/22_22533/15108163.html",
                "body > div.novel > h1",
                "#content",
                "body > div.novel > div:nth-child(5) > a:nth-child(3)",
                "姐妹花的最强兵王.txt");
        crawler.crawl();
    }
    public static void getTitle() throws IOException, InterruptedException {
    	GetTitle crawler=new GetTitle("https://www.biquge.info/22_22533/",
                "#list > dl", "q凡人修仙传仙界篇.txt");
        crawler.crawl();
    }
}
