/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : reasearch_kg

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-10-08 21:49:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user_tag`
-- ----------------------------
DROP TABLE IF EXISTS `user_tag`;
CREATE TABLE `user_tag` (
  `uid` varchar(255) NOT NULL,
  `tagName` varchar(255) NOT NULL,
  PRIMARY KEY (`uid`,`tagName`),
  KEY `tName` (`tagName`),
  CONSTRAINT `tName` FOREIGN KEY (`tagName`) REFERENCES `tag` (`tagName`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_tag_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_tag
-- ----------------------------
INSERT INTO `user_tag` VALUES ('7bd9b8f6cc3b36eabec0482b1e56ad97', 'CNN');
INSERT INTO `user_tag` VALUES ('7bd9b8f6cc3b36eabec0482b1e56ad97', 'GAN');
