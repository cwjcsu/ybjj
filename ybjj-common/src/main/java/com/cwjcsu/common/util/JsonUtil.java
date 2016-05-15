/*$Id: $
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author   date   comment
 * tongjun  2012-7-24  Created
*/
package com.cwjcsu.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class JsonUtil {
    private JsonUtil() {

    }

    public static final Gson GSON = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss Z").create();

    //Json字符串转HashMap
    public static Map<String, String> jsonDecode(String json) {
        Map<String, String> returnMap = new HashMap<String, String>();
        try {
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            returnMap = GSON.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnMap;
    }

    //Json字符串转HashMap
    public static Map<String, Object> fromJsonAsMap(String json) {
        if (StringUtil.isEmpty(json)) {
            return new HashMap<String, Object>(0);
        }
        return GSON.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }


    //HashMap转Json字符串
    public static String jsonEncode(Object mapObj) {
        return GSON.toJson(mapObj);
    }

    /**
     * 由于json输出成js字符串然后使用jquey.parseJson解析时，对转义过的'\'需要再次转义成双'\'
     * 参照考 http://api.jquery.com/jquery.parsejson/
     *
     * @param json
     * @return
     */
    public static String escapseForJsliteral(String json) {
        if (!StringUtils.isEmpty(json)) {
            return json.replaceAll("\\\\", "\\\\\\\\");
        }
        return json;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        return GSON.fromJson(json, clazz);
    }

    public static <T> T[] parseArray(String jsonStr, Class<T> componentType) {
        return GSON.fromJson(jsonStr, (Type) Array.newInstance(componentType, 0).getClass());
    }

    public static <T> T parseObject(String jsonStr, Class<T> componentType) {
        return GSON.fromJson(jsonStr, componentType);
    }


    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return GSON.toJson(src, typeOfSrc);
    }
}
