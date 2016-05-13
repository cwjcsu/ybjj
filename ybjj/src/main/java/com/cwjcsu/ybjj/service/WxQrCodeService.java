/*$$
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                          date   comment
 * chenweijun@skybility.com       2015/8/11  Created
 */
package com.cwjcsu.ybjj.service;/**
 * Created by atlas on 2015/8/11.
 */


import com.cwjcsu.ybjj.domain.WxQrCode;

/**
 * @author atlas
 **/
public interface WxQrCodeService {
    /**
     * @param secenKey      二维码关联的场景key
     * @param attachement   二维码关联的业务数据
     * @param expireSeconds 二维码的超时，如果为null则创建
     * @return
     */
    WxQrCode createWxQrCode(String secenKey, String attachement, Integer expireSeconds);

}
