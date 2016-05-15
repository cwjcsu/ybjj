package com.cwjcsu.common.util;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 增强：同时具备Map接口和Dict接口
 * 变更：构造函数DictImpl(Map map)采用putAll，而不是引用原来的map，所以，原来的代码如果有更改map的操作不会反应到当前Dict。
 */
public class DictImpl extends HashMap<String, Object> implements Dict, Serializable {

    private static final long serialVersionUID = -2027073374236399185L;

    public DictImpl() {
        super();
    }

    public DictImpl(Map map) {
        super(map);
    }

    public <T> T get(String key, T defaultValue) {
        T t = (T) get(key);
        if (t == null)
            return defaultValue;
        return t;
    }


    @Override
    public <T> T get(String key) {
        Object t = super.get(key);
        try {
            if (t instanceof String) {
                if (StringUtil.isEmpty((String) t))
                    return null;
            }
            return (T) t;
        } catch (ClassCastException e) {
            throw new ClassCastException("Type not match for property: " + key
                    + ":" + e.getMessage());
        }
    }

    @Override
    public <T> T require(String key) {
        T t = (T) get(key);
        if (t == null) {
            throwException(key);
        }
        return t;
    }

    @Override
    public Map getMap() {
        return this;
    }

    @Override
    public String getAsString(String key) {
        String value = getAsStringWithoutFilter(key);
        if (value != null) {
            // 因为长度不方便判断，暂不过滤输入
            // value = StringUtils.htmlEncode(value);
            // put(key, value);
        }
        return value;
    }

    @Override
    public String getAsString(String key, String defaultValue) {
        String v = getAsString(key);
        if (v == null) {
            return defaultValue;
        }
        return v;
    }

    @Override
    public String getAsStringWithoutFilter(String key) {
        Object t = get(key);
        if (t instanceof String)
            return (String) t;
        if (t == null)
            return null;
        return String.valueOf(t);
    }

    @Override
    public Boolean getAsBoolean(String key) {
        Object t = get(key);
        if (t instanceof Boolean)
            return (Boolean) t;
        if (t instanceof String) {
            if (StringUtil.isEmpty((String) t))
                return null;
        }
        if (t == null)
            return null;
        return Boolean.valueOf(t.toString());
    }

    @Override
    public boolean getAsBoolean(String key, boolean defaultValue) {
        Boolean r = getAsBoolean(key);
        if (r == null)
            return defaultValue;
        return r;
    }

    @Override
    public Integer getAsInteger(String key) {
        Object t = get(key);
        if (t instanceof Number)
            return ((Number) t).intValue();
        if (t instanceof String) {
            if (StringUtil.isEmpty((String) t))
                return null;
        }
        try {
            if (t != null)
                return Integer.valueOf(t.toString());
        } catch (NumberFormatException e) {
            throw throwException(key, t, "Integer");
        }
        return null;
    }

    @Override
    public int getAsInteger(String key, int defaultValue) {
        Integer v = getAsInteger(key);
        if (v == null) {
            return defaultValue;
        }
        return v;
    }

    @Override
    public Dict getAsDict(String key) {
        Map<String, Object> data = get(key);
        if (data != null) {
            return new DictImpl(data);
        } else {
            return null;
        }
    }

    @Override
    public List<Dict> getAsDictList(String key) {
        List<Map<String, Object>> data = get(key);
        List<Dict> dicts = new ArrayList<Dict>(data.size());
        for (Map<String, Object> d : data) {
            dicts.add(new DictImpl(d));
        }
        return dicts;
    }

    private RuntimeException throwException(String key, Object value,
                                            String type) {
        throw new IllegalArgumentException(
                String.format(
                        "The value \"%s\" for corresponding property : \"%s\" is not an %s.",
                        value, key, type));
    }

    private void throwException(String key) {
        throw new IllegalArgumentException(
                "got null value for required property : " + key);
    }

    @Override
    public Long getAsLong(String key) {
        Object t = get(key);

        if (t instanceof Number)
            return ((Number) t).longValue();
        if (t instanceof String) {
            if (StringUtil.isEmpty((String) t))
                return null;
        }
        try {
            if (t != null)
                return Long.valueOf(t.toString());
        } catch (NumberFormatException e) {
            throw throwException(key, t, "Long");
        }
        return null;
    }

    @Override
    public long getAsLong(String key, long defaultValue) {
        Long v = getAsLong(key);
        if (v == null) {
            return defaultValue;
        }
        return v;
    }

    @Override
    public Float getAsFloat(String key) {
        Object t = get(key);

        if (t instanceof Number)
            return ((Number) t).floatValue();
        if (t instanceof String) {
            if (StringUtil.isEmpty((String) t))
                return null;
        }
        try {
            if (t != null)
                return Float.valueOf(t.toString());
        } catch (NumberFormatException e) {
            throw throwException(key, t, "Float");
        }
        return null;
    }

    @Override
    public Double getAsDouble(String key) {
        Object t = get(key);
        if (t instanceof Number)
            return ((Number) t).doubleValue();
        if (t instanceof String) {
            if (StringUtil.isEmpty((String) t))
                return null;
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
        SimpleDateFormat smp = new SimpleDateFormat(pattern);
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
    public int requireInteger(String key) {
        Integer value = getAsInteger(key);
        if (value == null) {
            throwException(key);
        }
        return value.intValue();
    }

    @Override
    public long requireLong(String key) {
        Long value = getAsLong(key);
        if (value == null) {
            throwException(key);
        }
        return value.longValue();

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
        return value.booleanValue();
    }

    @Override
    public Date requireDate(String key, String pattern) {
        String dateStr = getAsString(key);
        if (dateStr == null) {
            throwException(key);
        }
        SimpleDateFormat smp = new SimpleDateFormat(pattern);
        try {
            return smp.parse(dateStr);
        } catch (ParseException e) {
            throwException(key, dateStr, "date");
            return null;
        }
    }

    @Override
    public Object put(String key, Object value) {
        return super.put(key, value);
    }

    @Override
    public Object update(String key, Object value) {
        return super.put(key, value);
    }

    @Override
    public void update(Map<String, Object> mappings) {
        super.putAll(mappings);
    }

    public DictImpl of(String key, Object value) {
        put(key, value);
        return this;
    }
}
