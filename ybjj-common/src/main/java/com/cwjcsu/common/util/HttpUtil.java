package com.cwjcsu.common.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.StringTokenizer;

public final class HttpUtil {
	private HttpUtil(){
		
	}

    public static String getRemoteIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ipAddress)
                || ("unknown".equalsIgnoreCase(ipAddress))) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ipAddress)
                || ("unknown".equalsIgnoreCase(ipAddress))) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if ((ipAddress != null) && (ipAddress.length() > 15)
                && (ipAddress.indexOf(",") > 0)) {
            StringTokenizer st = new StringTokenizer(ipAddress, ",");
            while(st.hasMoreTokens()) {
                ipAddress = st.nextToken();
                if(!"unknown".equalsIgnoreCase(ipAddress)) {
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(ipAddress)
                || ("unknown".equalsIgnoreCase(ipAddress))) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null
                && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString());
    }
}
