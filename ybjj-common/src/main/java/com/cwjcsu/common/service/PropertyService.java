package com.cwjcsu.common.service;

import java.util.Date;
import java.util.Map;

/**
 * 
 * @author atlas
 * @date 2012-9-29
 */
public interface PropertyService {

	String getAsString(String key);

	Integer getAsInteger(String key);

	Long getAsLong(String key);

	Double getAsDouble(String key);

	Boolean getAsBoolean(String key);

	String requireString(String key);

	Integer requireInteger(String key);

	Long requireLong(String key);

	double requireDouble(String key);

	Boolean requireBoolean(String key);
	
	/**
	 * 直接获取一个date
	 * @param key
	 * @param pattern 参照java.text.SimpleDateFormat里的那个pattern
	 * @return
	 */
	Date getAsDate(String key, String pattern);
	
	Date requireDate(String key, String pattern);

	Map getMap();
	
	boolean hasProperty(String key);
}
