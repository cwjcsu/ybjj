
SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

SET NAMES utf8mb4;

CREATE DATABASE `ybjj` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

use ybjj;


DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `avatar` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `gender` tinyint(4) NOT NULL DEFAULT '0' COMMENT '性别，未指定，男，女',
  `user_status` tinyint(4) NOT NULL COMMENT '用户状态，未激活， 已激活， 禁用，删除',
  `online` tinyint(1) NOT NULL COMMENT '是否在线',
  `user_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '用户类型',
  `signature` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '签名',
  `company` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公司',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `location` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '位置',
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_email_Idx` (`email`),
  UNIQUE KEY `users_name_Idx` (`name`),
  UNIQUE KEY `users_phone_Idx` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';




DROP TABLE IF EXISTS `wx_qrcode`;
CREATE TABLE `wx_qrcode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ticket` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_id` int(11) DEFAULT NULL COMMENT '二维码的所有者,如用户或者网关等',
  `open_id` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '扫描二维码的微信用户',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '二维码状态：0:等待扫描;1:已经扫描;2:业务成功;3:业务失败;4:二维码过期',
  `remark` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expire_seconds` int(11) NOT NULL DEFAULT '-1' COMMENT '超时秒数,-1表示永久二维码',
  `attachement` varchar(4096) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二维码关联业务的附加信息',
  `scene_key` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `wx_qrcode_ticket_Idx` (`ticket`(128)),
  UNIQUE KEY `wx_qrcode_uuid_Idx` (`uuid`(128))
) ENGINE=InnoDB AUTO_INCREMENT=1687 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信二维码';

DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
  `user_id` int(11) NOT NULL,
  `password` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `password_salt` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '加密的盐',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账户表';

DROP TABLE IF EXISTS `open_account`;
CREATE TABLE `open_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '行云网账户id,Users.id',
  `oauth_type` tinyint(4) NOT NULL COMMENT '账号类型,0:qq,1:weixin;2:weibo',
  `open_id` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `gender` tinyint(4) DEFAULT NULL COMMENT '性别',
  `avatar` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像地址',
  `create_time` datetime DEFAULT NULL COMMENT '开放账户绑定的时间',
  `city` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '城市',
  `province` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省份',
  `location` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '位置',
  `home` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开放账号主页',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uqi_oauth_type_open_id` (`oauth_type`,`open_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开放账户';


CREATE TABLE `login_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '登录用户id',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `login_type` tinyint(4) NOT NULL COMMENT '登录方式',
  `result` tinyint(4) NOT NULL COMMENT '结果',
  `client_ip` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录时的客户端IP',
  `user_agent` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浏览器的userAgent',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2633 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `user_session` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `token_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'json web token 的id',
  `expire_time` datetime NOT NULL COMMENT 'http session的过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4459 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `user_web_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `token_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'web token的tokenId',
  `expire_time` datetime NOT NULL COMMENT 'web token的存活期',
  `delete_on_session_destroy` tinyint(1) NOT NULL COMMENT 'web token是否随http session的销毁而销毁',
  `open_account_id` int(11) DEFAULT NULL COMMENT 'jwt对应的第三方帐号id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6303 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户jwt表';

