/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/12  Created
 */
package com.cwjcsu.common.util;

import java.util.Map;

/**
 * @author Atlas
 */
public class RequestResult extends DictImpl {

    public RequestResult() {
        success();
    }

    public RequestResult(boolean success, String msg) {
        if (success) {
            success(msg);
        } else {
            fail(msg);
        }
    }

    public RequestResult(Map m) {
        super(m);
    }

    public RequestResult fail() {
        return fail((String) null);
    }

    public RequestResult fail(String error) {
        put("success", false);
        put("msg", error);
        return this;
    }

    public RequestResult fail(Throwable error) {
        String msg = error.getMessage();
        if (msg == null) {
            error = error.getCause();
            msg = error != null ? error.getMessage() : "null";
        }
        return fail(msg);
    }

    public RequestResult msg(String msg) {
        put("msg", msg);
        return this;
    }

    public RequestResult success() {
        return success(null);
    }

    public RequestResult success(String key, Object value) {
        put(key, value);
        return success();
    }

    public RequestResult fail(String key, Object value) {
        put(key, value);
        return fail();
    }

    public RequestResult of(String key, Object value) {
        put(key, value);
        return this;
    }

    public RequestResult success(String message) {
        put("success", true);
        if (message != null) {
            put("msg", message);
        }
        return this;
    }

    public String getMsg() {
        return get("msg") + "";
    }

    public Dict asDict() {
        return new DictImpl(this);
    }

    public boolean isSuccess() {
        return Boolean.TRUE.equals(get("success"));
    }
}
