package com.webspider.service;

import java.util.List;

public interface BaseService {

	List<String> getResource() throws Exception ;

	String getContent(String path)throws Exception;

}
