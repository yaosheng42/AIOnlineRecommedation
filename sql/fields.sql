/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : reasearch_kg

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-10-08 21:47:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `fields`
-- ----------------------------
DROP TABLE IF EXISTS `fields`;
CREATE TABLE `fields` (
  `id` varchar(5) NOT NULL,
  `chineseName` varchar(255) NOT NULL,
  `englishName` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of fields
-- ----------------------------
INSERT INTO `fields` VALUES ('00010', '人工智能', 'Artificial Intelligence');
INSERT INTO `fields` VALUES ('00020', '计算机图形', 'Computer graphics(images)');
INSERT INTO `fields` VALUES ('00030', '计算机视觉', 'Coputer vision');
INSERT INTO `fields` VALUES ('00040', '数据挖掘', 'Data mining');
INSERT INTO `fields` VALUES ('00050', '机器学习', 'Machine learing');
INSERT INTO `fields` VALUES ('00060', '自然语言处理', 'Natural laguage processing');
INSERT INTO `fields` VALUES ('00070', '模式识别', 'Pattern Recoginition');
INSERT INTO `fields` VALUES ('00080', '万维网', 'World Wide Web');
INSERT INTO `fields` VALUES ('00090', '语言识别', 'Speech recognition');
INSERT INTO `fields` VALUES ('00100', '语义网', 'Semantic web');
INSERT INTO `fields` VALUES ('00110', '知识图谱', 'Konwledge Graph');
INSERT INTO `fields` VALUES ('00120', '信息检索', 'information retrieval');
