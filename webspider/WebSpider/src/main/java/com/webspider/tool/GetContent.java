package com.webspider.tool;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

public class GetContent {
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

	public GetContent(String url, String titleSelector, String contentSelector) {
		this.url = url;
		this.titleSelector = titleSelector;
		this.contentSelector = contentSelector;
	}

	public GetContent(String url, String titleSelector, String contentSelector, String nextChapterSelector) {
		this.url = url;
		this.titleSelector = titleSelector;
		this.contentSelector = contentSelector;
		this.nextChapterSelector = nextChapterSelector;
	}

	public GetContent(String url, String titleSelector, String contentSelector, String nextChapterSelector,
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
		crawl(200);
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
			// 初始化
			write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true)));
			int count = 10;// 文件写入的刷新间隔
			// 遍历所有网页
			while (true) {
				// 获取文档
				Document document = getDocument(url);
				// 获取小说内容
				boolean chapter = getChapter(document);
				if (!chapter) {
					break;
				}
				// 判断html是否有“下一页”的链接
				Element link = document.selectFirst(nextChapterSelector);
				if (link == null) {// 没有“下一页”
					// 爬取完毕，跳出循环
					break;
				}
				// 获取下一页链接
				url = link.attr("abs:href");
				// 每十章刷新一次
				if (--count == 0) {
					write.flush();
					count = 10;
				}
				// 延迟
				if (delay > 0) {
					Thread.sleep(delay);
				}
			}
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
		// 获取标题
		Element titleEle = document.selectFirst(titleSelector);
		if (titleEle == null) {
			return false;
		}
		String title = getContent(titleEle).trim();
		write.write(title + "\n");

		System.out.println("已获取章节：" + title);

		// 获取正文
		Element contentEle = document.selectFirst(contentSelector);
		String content = getContent(contentEle).trim();
		write.write(content + "\n");
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
				stringBuilder.append(node.text().trim() + "<br>");
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

	public String getcontent() throws Exception {
		// 初始化
		String content = "";
		// 遍历所有网页
		// 获取文档
		Document document = getDocument(url);
		// 获取小说内容
		// 获取标题
		Element titleEle = document.selectFirst(titleSelector);
		content = getContent(titleEle);
		
		// 获取正文
		Element contentEle = document.selectFirst(contentSelector);
		content = content+getContent(contentEle);
		
		return content;
	}

}
