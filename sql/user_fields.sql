/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : reasearch_kg

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-10-08 21:48:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user_fields`
-- ----------------------------
DROP TABLE IF EXISTS `user_fields`;
CREATE TABLE `user_fields` (
  `uid` varchar(255) NOT NULL,
  `fid` varchar(255) NOT NULL,
  PRIMARY KEY (`uid`,`fid`),
  KEY `user_field_fid` (`fid`) USING BTREE,
  CONSTRAINT `user_fields_ibfk_1` FOREIGN KEY (`fid`) REFERENCES `fields` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_fields_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of user_fields
-- ----------------------------
INSERT INTO `user_fields` VALUES ('300ac5a5a98428a18f381667305e0f03', '00010');
INSERT INTO `user_fields` VALUES ('503e9061152b94b4c6e7e57db53f70bc', '00010');
INSERT INTO `user_fields` VALUES ('69cf4f595960ad0e168a6d91397637b3', '00010');
INSERT INTO `user_fields` VALUES ('7bd9b8f6cc3b36eabec0482b1e56ad97', '00010');
INSERT INTO `user_fields` VALUES ('69cf4f595960ad0e168a6d91397637b3', '00030');
INSERT INTO `user_fields` VALUES ('300ac5a5a98428a18f381667305e0f03', '00040');
INSERT INTO `user_fields` VALUES ('503e9061152b94b4c6e7e57db53f70bc', '00040');
INSERT INTO `user_fields` VALUES ('300ac5a5a98428a18f381667305e0f03', '00050');
INSERT INTO `user_fields` VALUES ('503e9061152b94b4c6e7e57db53f70bc', '00050');
INSERT INTO `user_fields` VALUES ('69cf4f595960ad0e168a6d91397637b3', '00050');
INSERT INTO `user_fields` VALUES ('300ac5a5a98428a18f381667305e0f03', '00060');
INSERT INTO `user_fields` VALUES ('503e9061152b94b4c6e7e57db53f70bc', '00060');
INSERT INTO `user_fields` VALUES ('69cf4f595960ad0e168a6d91397637b3', '00060');
INSERT INTO `user_fields` VALUES ('300ac5a5a98428a18f381667305e0f03', '00070');
INSERT INTO `user_fields` VALUES ('300ac5a5a98428a18f381667305e0f03', '00080');
INSERT INTO `user_fields` VALUES ('503e9061152b94b4c6e7e57db53f70bc', '00080');
INSERT INTO `user_fields` VALUES ('503e9061152b94b4c6e7e57db53f70bc', '00090');
INSERT INTO `user_fields` VALUES ('300ac5a5a98428a18f381667305e0f03', '00100');
INSERT INTO `user_fields` VALUES ('503e9061152b94b4c6e7e57db53f70bc', '00100');
INSERT INTO `user_fields` VALUES ('69cf4f595960ad0e168a6d91397637b3', '00100');
INSERT INTO `user_fields` VALUES ('300ac5a5a98428a18f381667305e0f03', '00110');
INSERT INTO `user_fields` VALUES ('503e9061152b94b4c6e7e57db53f70bc', '00110');
INSERT INTO `user_fields` VALUES ('69cf4f595960ad0e168a6d91397637b3', '00110');
INSERT INTO `user_fields` VALUES ('300ac5a5a98428a18f381667305e0f03', '00120');
INSERT INTO `user_fields` VALUES ('503e9061152b94b4c6e7e57db53f70bc', '00120');
