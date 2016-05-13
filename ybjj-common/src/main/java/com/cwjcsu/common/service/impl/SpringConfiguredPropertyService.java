package com.cwjcsu.common.service.impl;

import com.cwjcsu.common.service.PropertyService;
import com.cwjcsu.common.util.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.StringValueResolver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author atlas
 * @date 2012-9-29
 */
public class SpringConfiguredPropertyService extends
		PropertyPlaceholderConfigurer implements PropertyService {
	private Properties props;
	private StringValueResolver valueResolver;
	private static Map<String, SimpleDateFormat> SDFS = new ConcurrentHashMap<String, SimpleDateFormat>();

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactory, Properties props)
			throws BeansException {
		super.processProperties(beanFactory, props);
		this.props = props;
	}
	
	@Override
	protected void doProcessProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			StringValueResolver valueResolver) {
		super.doProcessProperties(beanFactoryToProcess, valueResolver);
		this.valueResolver = valueResolver; 
	}

	@Override
	public String getAsString(String key) {
		Object t = props.get(key);
		if (t instanceof String){
			return this.valueResolver.resolveStringValue((String) t);
		}
		if (t == null)
			return null;
		return this.valueResolver.resolveStringValue(String.valueOf(t));
	}

	@Override
	public Boolean getAsBoolean(String key) {
		Object t = props.get(key);
		if (t instanceof Boolean)
			return (Boolean) t;
		if (t instanceof String) {
			if (StringUtil.isEmpty((String) t))
				return null;
			t = this.valueResolver.resolveStringValue((String) t);
		}
		if (t == null)
			return null;
		return Boolean.valueOf(t.toString());
	}

	@Override
	public Integer getAsInteger(String key) {
		Object t = props.get(key);
		if (t instanceof Number)
			return ((Number) t).intValue();
		if (t instanceof String) {
			if (StringUtil.isEmpty((String) t))
				return null;
			t = this.valueResolver.resolveStringValue((String) t);
		}
		try {
			if (t != null)
				return Integer.valueOf(t.toString());
		} catch (NumberFormatException e) {
			throw throwException(key, t, "Integer");
		}
		return null;
	}

	private RuntimeException throwException(String key, Object value,
			String type) {
		throw new IllegalArgumentException(
				String.format(
						"The value \"%s\" of corresponding property \"%s\" is not an %s.",
						value, key, type));
	}

	private void throwException(String key) {
		throw new IllegalArgumentException(
				"got null value of required property " + key);
	}

	@Override
	public Long getAsLong(String key) {
		Object t = props.get(key);

		if (t instanceof Number)
			return ((Number) t).longValue();
		if (t instanceof String) {
			if (StringUtil.isEmpty((String) t))
				return null;
			t = this.valueResolver.resolveStringValue((String) t);
		}
		try {
			if (t != null)
				return Long.valueOf(t.toString());
		} catch (NullPointerException e) {
			throw throwException(key, t, "Long");
		}
		return null;
	}

	@Override
	public Double getAsDouble(String key) {
		Object t = props.get(key);
		if (t instanceof Number)
			return ((Number) t).doubleValue();
		if (t instanceof String) {
			if (StringUtil.isEmpty((String) t))
				return null;
			t = this.valueResolver.resolveStringValue((String) t);
		}
		try {
			if (t != null)
				return Double.valueOf(t.toString());
		} catch (NumberFormatException e) {
			throw throwException(key, t, "Double");
		}
		return null;
	}

	@Override
	public Date getAsDate(String key, String pattern) {
		String dateStr = getAsString(key);
		if (dateStr == null) {
			return null;
		}
		SimpleDateFormat smp = SDFS.get(pattern);
		if (smp == null) {
			smp = new SimpleDateFormat(pattern);
			SDFS.put(pattern, smp);
		}
		try {
			return smp.parse(dateStr);
		} catch (ParseException e) {
			throwException(key, dateStr, "date");
		}
		return null;
	}

	@Override
	public String requireString(String key) {
		String value = getAsString(key);
		if (value == null) {
			throwException(key);
		}
		return value;
	}

	@Override
	public Integer requireInteger(String key) {
		Integer value = getAsInteger(key);
		if (value == null) {
			throwException(key);
		}
		return value;
	}

	@Override
	public Long requireLong(String key) {
		Long value = getAsLong(key);
		if (value == null) {
			throwException(key);
		}
		return value;

	}

	@Override
	public double requireDouble(String key) {
		Double value = getAsDouble(key);
		if (value == null) {
			throwException(key);
		}
		return value.doubleValue();
	}

	@Override
	public Boolean requireBoolean(String key) {
		Boolean value = getAsBoolean(key);
		if (value == null)
			throwException(key);
		return value;
	}

	@Override
	public Date requireDate(String key, String pattern) {
		Date date = getAsDate(key, pattern);
		if (date == null) {
			throwException(key);
		}
		return date;
	}

	@Override
	public Map getMap() {
		return this.props;
	}
	
	@Override
	public boolean hasProperty(String key){
		return this.props.containsKey(key);
	}

    public Properties getProps() {
        return props;
    }

    public StringValueResolver getValueResolver() {
        return valueResolver;
    }
}
