/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : reasearch_kg

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-10-08 21:48:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `uname` varchar(255) NOT NULL,
  `upassword` varchar(255) NOT NULL,
  `mailbox` varchar(255) NOT NULL,
  `utype` int(11) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('300ac5a5a98428a18f381667305e0f03', 'JuneShi', 'kse_123456', 'juneshi0315@qq.com', '2', null);
INSERT INTO `user` VALUES ('503e9061152b94b4c6e7e57db53f70bc', 'Guilin', '654321', 'gqi@seu.edu.cn', '5', null);
INSERT INTO `user` VALUES ('69cf4f595960ad0e168a6d91397637b3', 'xuhuapeng', 'xuhuapeng', 'cx4765@qq.com', '2', null);
INSERT INTO `user` VALUES ('7bd9b8f6cc3b36eabec0482b1e56ad97', 'yaosheng', 'yaosheng', '220151573@seu.edu.cn', '2', 'http://com.yaosheng');
