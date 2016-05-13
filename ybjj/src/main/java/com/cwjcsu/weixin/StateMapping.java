/*$Id: $
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2014年7月25日  Created
*/
package com.cwjcsu.weixin;

import java.lang.annotation.*;

/**
 * @author atlas
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StateMapping {

    /**
     * 可以处理的请求
     * @return
     */
    String[] state();

    /**
     * 这个请求是否需要绑定行云服务账户
     *
     * @return
     */
    boolean requireBind() default true;

    /**
     * 这个请求是否需要登录
     *
     * @return
     */
//    boolean requireLogin() default true;
}
