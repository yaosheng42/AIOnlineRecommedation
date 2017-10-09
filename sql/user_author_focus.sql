/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : reasearch_kg

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-10-08 21:48:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user_author_focus`
-- ----------------------------
DROP TABLE IF EXISTS `user_author_focus`;
CREATE TABLE `user_author_focus` (
  `uid` varchar(255) NOT NULL,
  `aid` int(11) NOT NULL,
  PRIMARY KEY (`uid`,`aid`),
  KEY `user_author_aid` (`aid`),
  CONSTRAINT `user_author_aid` FOREIGN KEY (`aid`) REFERENCES `author` (`aid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_author_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_author_focus
-- ----------------------------
