package com.webspider.tool;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class GetTitle {
	/**
	 * 起始页
	 */
	private String url;
	/**
	 * 每一章标题的选择器
	 */
	private String titleSelector;
	/**
	 * 每一章正文的选择器
	 */
	private String contentSelector;
	/**
	 * 下一章链接的css选择器
	 */
	private String nextChapterSelector;
	/**
	 * 爬取后文件保存位置和命名，默认当前位置
	 */
	private String filename = "./小说.txt";
	/**
	 * 是否以添加的方式写入文件，默认false
	 */
	private boolean append = false;
	/**
	 * 用于存储到文件中
	 */
	private BufferedWriter write;

	public GetTitle(String url, String contentSelector, String filename) {
		this.url = url;
		this.contentSelector = contentSelector;
		this.filename = filename;
	}

	public GetTitle(String url, String titleSelector, String contentSelector, String nextChapterSelector) {
		this.url = url;
		this.titleSelector = titleSelector;
		this.contentSelector = contentSelector;
		this.nextChapterSelector = nextChapterSelector;
	}

	public GetTitle(String url, String titleSelector, String contentSelector, String nextChapterSelector,
			String filename) {
		this.url = url;
		this.titleSelector = titleSelector;
		this.contentSelector = contentSelector;
		this.nextChapterSelector = nextChapterSelector;
		this.filename = filename;
	}

	/**
	 * 爬取数据，默认延迟200ms
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void crawl() throws IOException, InterruptedException {
		crawl(0);
	}

	/**
	 * 爬取数据，异常时重试10次
	 * 
	 * @param delay
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void crawl(int delay) throws IOException, InterruptedException {
		int count = 10;// 重试次数
		boolean flag = true;
		while (flag) {
			try {
				_crawl(delay);
				flag = false;
			} catch (Exception e) {
				flag = false;
				if (--count != 0) {
					System.out.println("莫名错误，原因：" + e.getMessage());
					System.out.println("开始第" + (10 - count) + "次重试");
					flag = true;
					Thread.sleep(1000);

					// 设置为添加模式，防止文件被覆盖
					this.append = true;
				} else {
					throw e;
				}
			}
		}
	}

	/**
	 * 设置append属性
	 * 
	 * @param append
	 */
	public void setAppend(Boolean append) {
		this.append = append;
	}

	/**
	 * 开始爬出数据
	 * 
	 * @param delay 每次读取网页的延迟时间,单位ms，用于反爬虫
	 * @throws IOException 获取网页和保存文件时错误，抛出该异常
	 */
	private void _crawl(int delay) throws IOException, InterruptedException {
		try {
			/*
			 * File f = new File(filename); if(f.exists()) { f.delete(); }
			 */
			// 初始化
			//write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true)));
			// 遍历所有网页
			// 获取文档
			Document document = getDocument(url);
			// 获取小说内容
			getChapter(document);

			//write.flush();
		} finally {
			write.close();
		}
	}

	/**
	 * 获取该章节的标题、正文，并保存在文件中
	 * 
	 * @param document
	 * @throws IOException
	 */
	private boolean getChapter(Document document) throws IOException {
		// 获取正文
		Element contentEle = document.selectFirst(contentSelector);
		boolean flag = true;
		int i = 0;
		while (flag) {
			Elements dd = contentEle.getElementsByTag("dd");
			List<String> eachText = dd.eachText();
			int size = eachText.size();
			
			Element child = contentEle.child(i);
			Elements children = child.children();
			String text = children.text();
			String attr = children.attr("href");
			write.write(children.toString() + "\n");
			i++;
			if(i>=size-10) {
				flag=false;
			}
		}
		return true;
	}

	/**
	 * 获取该元素中的文本内容。 会遍历子元素
	 * 
	 * @param element
	 * @return
	 */
	private String getContent(Element element) {
		List<TextNode> textNodes = element.textNodes();
		StringBuilder stringBuilder = new StringBuilder();
		textNodes.forEach((node) -> {
			if (!node.text().trim().isEmpty()) {
				stringBuilder.append(node.text().trim() + "\n");
			}
		});
		return stringBuilder.toString();
	}

	/**
	 * 从url上获取文档，为了防止反爬虫，这是一些头字段 如果失败，会重试10次
	 * 
	 * @param url
	 * @return
	 */
	private Document getDocument(String url) throws IOException {
		int count = 10;// 重试次数
		boolean flag = true;
		Document document = null;
		while (flag) {
			try {
				document = Jsoup.connect(url).userAgent(
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
						.get();
				flag = false;
			} catch (IOException e) {
				flag = false;
				if (count-- != 0) {
					System.out.println("网页获取失败，原因：" + e.getMessage());
					System.out.println("开始第" + (10 - count) + "次重试");
				} else {
					throw e;
				}
			}
		}
		return document;
	}

	public List<String> get() throws Exception{
		
		List<String> list = new ArrayList<String>();
		Document document = getDocument(url);
		
		Element contentEle = document.selectFirst(contentSelector);
		boolean flag = true;
		int i = 0;
		while (flag) {
			Elements dd = contentEle.getElementsByTag("dd");
			List<String> eachText = dd.eachText();
			int size = eachText.size();
			
			Element child = contentEle.child(i);
			Elements children = child.children();
			//String text = children.text();
			String attr = children.attr("href");
			children.attr("href", "/page/"+attr);
			children.attr("target", "_blank");
			list.add(children.toString());
			i++;
			if(i>=size) {
				flag=false;
			}
		}
		return list;
		
		
	}
}
