package com.cwjcsu.common.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 装饰一个Map，提供便利方法获取值，所有getXXX方法均返回对象，可能是null，所有require均会返回非null的值，如果值是null，会抛出异常。
 * @author atlas
 *
 */
public interface Dict {
	
	<T> T get(String key, T defaultValue);

	<T> T get(String key);

	String getAsString(String key);

    String getAsString(String key, String defaultValue);

	Integer getAsInteger(String key);
	
	int getAsInteger(String key, int defaultValue);

	Long getAsLong(String key);
	
	long getAsLong(String key, long defaultValue);

	Double getAsDouble(String key);

	Boolean getAsBoolean(String key);

    boolean getAsBoolean(String key, boolean defaultValue);

    Dict getAsDict(String key);

    List<Dict> getAsDictList(String key);

	String requireString(String key);

	<T> T require(String key);

	int requireInteger(String key);

	long requireLong(String key);

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

	String getAsStringWithoutFilter(String key);

	Float getAsFloat(String key);

    Object put(String key, Object value);

    Object update(String key, Object value);

    void update(Map<String, Object> mappings);
}
