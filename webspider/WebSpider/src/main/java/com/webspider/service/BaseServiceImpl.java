package com.webspider.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.webspider.tool.GetContent;
import com.webspider.tool.GetTitle;

@Service
public class BaseServiceImpl implements BaseService{

	@Override
	public List<String> getResource() throws Exception {
		GetTitle crawler=new GetTitle("https://www.biquge.info/22_22533/",
                "#list > dl", "q凡人修仙传仙界篇.txt");
        
		List<String> list = crawler.get();
		
		return list;
	}

	@Override
	public String getContent(String path) throws Exception {
		GetContent crawler=new GetContent("https://www.biquge.info/22_22533/"+path+".html",
                "#wrapper > div.content_read > div.box_con > div.bookname > h1",
                "#content",
                "q凡人修仙传仙界篇.txt");
        String content = crawler.getcontent();
		return content;
	}

}
