package com.webspider.contorl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.webspider.service.BaseService;

@Controller
public class BaseControl {

	@Autowired
	private BaseService baseService;
	
	@RequestMapping("/")
	public String index() {
		return "/WEB-INF/jsp/spider.jsp";
	}
	
	@ResponseBody
	@RequestMapping("/getResource/index")
	public List<String> getResource(){
		List<String> resource = new ArrayList<String>();
		try {
			 resource = this.baseService.getResource();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resource;
	}
	
	@ResponseBody
	@RequestMapping(value="/page/{path}",produces = "text/html;charset=utf-8")
	public String getContent(@PathVariable String path) {
		String str="";
		try {
			str = this.baseService.getContent(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	@ResponseBody
	@RequestMapping("/log")
	public String log(String path) {
		System.out.println("log");
		return null;
	}
}
