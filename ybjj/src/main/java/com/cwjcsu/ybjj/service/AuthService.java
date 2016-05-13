package com.cwjcsu.ybjj.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * Created by jeremy on 15/7/7.
 */
public interface AuthService {
    /**
     * 为一个tokenId 生成 json web token (JWT)
     * @param tokenId
     * @return
     */
    String getJWTForTokenId(String tokenId);
    Claims getJWTBody(String jwt);

    Jws<Claims> parseJwt(String data);
}
