/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : reasearch_kg

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-10-08 21:48:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user_paper_question`
-- ----------------------------
DROP TABLE IF EXISTS `user_paper_question`;
CREATE TABLE `user_paper_question` (
  `uid` varchar(255) NOT NULL,
  `pid` varchar(255) NOT NULL,
  `question` longtext,
  PRIMARY KEY (`uid`,`pid`),
  KEY `question_pid` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_paper_question
-- ----------------------------
INSERT INTO `user_paper_question` VALUES ('1c763dbd7ee872abc05aa1baedc157a7', '1509.0192', 'ddd');
