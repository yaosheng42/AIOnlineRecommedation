/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : reasearch_kg

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-10-08 21:48:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user_paper_note`
-- ----------------------------
DROP TABLE IF EXISTS `user_paper_note`;
CREATE TABLE `user_paper_note` (
  `uid` varchar(255) NOT NULL,
  `pid` varchar(255) NOT NULL,
  `context` longtext,
  `contextURI` varchar(255) DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `paper_source` varchar(255) DEFAULT NULL,
  `keywords` varchar(255) DEFAULT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `paper_direction` longtext,
  `paper_model` longtext,
  `paper_problem` longtext,
  `paper_relatedwork` longtext,
  `paper_review` longtext,
  `author_place` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`uid`,`pid`),
  KEY `pid` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_paper_note
-- ----------------------------
INSERT INTO `user_paper_note` VALUES ('7bd9b8f6cc3b36eabec0482b1e56ad97', 'http://seu/kse/owl/paper/1504.01807', null, null, 'dad', 'cac', 'ca', '', null, '', null, '', '', 'cac');
INSERT INTO `user_paper_note` VALUES ('7bd9b8f6cc3b36eabec0482b1e56ad97', 'http://seu/kse/owl/paper/1509.01920', 'da', 'da', 'dad', 'vav', 'vav', '', '', 'vas', null, 'da', 'da', 'fafaf');
INSERT INTO `user_paper_note` VALUES ('7bd9b8f6cc3b36eabec0482b1e56ad97', 'http://seu/kse/owl/paper/1605.07740', '', '', 'dad', 'fa', '', '', '', '', '', '', '', '');
INSERT INTO `user_paper_note` VALUES ('7bd9b8f6cc3b36eabec0482b1e56ad97', 'http://seu/kse/owl/paper/1705.08843', null, null, 'ada', 'udahd', 'dhadu', 'dauhdahu', null, 'dadafa', null, 'dadad', 'dadadawff', 'audaud');
