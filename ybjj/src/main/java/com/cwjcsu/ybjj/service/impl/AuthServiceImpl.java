package com.cwjcsu.ybjj.service.impl;


import com.cwjcsu.common.util.IOUtil;
import com.cwjcsu.ybjj.domain.User;
import com.cwjcsu.ybjj.domain.UserWebToken;
import com.cwjcsu.ybjj.mapper.UserMapper;
import com.cwjcsu.ybjj.mapper.UserWebTokenMapper;
import com.cwjcsu.ybjj.service.AuthService;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.ObjectInputStream;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeremy on 15/7/7.
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserWebTokenMapper userWebTokenMapper;

    public static final String ISSUER = "cloudbility.com";
    private static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private static Key authSigningKey;

    private static Key getAuthSigningKey() {
        if(authSigningKey == null){
            synchronized (AuthServiceImpl.class){
                if(authSigningKey == null){
                    authSigningKey = loadAuthSigningKey();
                }
            }
        }
        return authSigningKey;
    }

    private static Key loadAuthSigningKey() {
        Key key = null;
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(AuthServiceImpl.class.getResourceAsStream("/authsigning.key"));
            key = (Key) oin.readObject();
        } catch (Exception e) {
            logger.error("load auth signing key failed", e);
        } finally {
           IOUtil.closeQuietly(oin);
        }
        return key;
    }

    public String getJWTForTokenId(String tokenId) {
        Assert.notNull(tokenId);
        UserWebToken userWebToken = userWebTokenMapper.selectByTokenId(tokenId);
        Assert.notNull(userWebToken);
        User user = userMapper.selectByPrimaryKey(userWebToken.getUserId());
        Assert.notNull(user);
        Assert.notNull(userWebToken.getTokenId());
        Assert.notNull(userWebToken.getExpireTime());

        Map<String, Object> claims = new HashMap<String , Object>();
        claims.put("username", user.getName());
        claims.put("userId", user.getId());
        claims.put("nickname", user.getNickname());

        String token = Jwts.builder().setIssuer(ISSUER)
                .setClaims(claims).setId(userWebToken.getTokenId())
                .setExpiration(userWebToken.getExpireTime())
                .signWith(SignatureAlgorithm.HS512, AuthServiceImpl.getAuthSigningKey()).compact();
        return token;
    }
    
    public Claims getJWTBody(String jwt) throws ExpiredJwtException, MalformedJwtException, SignatureException {
        return this.parseJwt(jwt).getBody();
    }

    @Override
    public Jws<Claims> parseJwt(String data) throws ExpiredJwtException, MalformedJwtException, SignatureException{
        return Jwts.parser().setSigningKey(AuthServiceImpl.getAuthSigningKey()).parseClaimsJws(data);
    }
}
