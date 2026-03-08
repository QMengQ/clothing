/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80013 (8.0.13)
 Source Host           : localhost:3306
 Source Schema         : clothing_db

 Target Server Type    : MySQL
 Target Server Version : 80013 (8.0.13)
 File Encoding         : 65001

 Date: 08/03/2026 13:47:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for clothing
-- ----------------------------
DROP TABLE IF EXISTS `clothing`;
CREATE TABLE `clothing`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `size` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `season` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `purchase_date` date NULL DEFAULT NULL,
  `last_wear_date` date NULL DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `storage_date` date NULL DEFAULT NULL,
  `wear_frequency` int(11) NULL DEFAULT NULL,
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `image_urls` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `price` double NULL DEFAULT NULL,
  `rental_price` double NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_purchase_date`(`purchase_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of clothing
-- ----------------------------
INSERT INTO `clothing` VALUES (3, '2', '1', '1', '春季', '良好', '1', '2026-02-12', '2026-02-27', 2, 'http://localhost:8080/uploads/31470b20-fc3c-4ede-b07d-54e969391396.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `clothing` VALUES (4, '1', '1', '1', '秋季', '较差', '1', '2026-02-05', '2026-02-28', 2, 'http://localhost:8080/uploads/23d92dcb-1d03-4cd2-ad20-46f058cdd050.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `clothing` VALUES (5, '1', '1', '1', '春季', '良好', '3', '2026-02-03', '2026-02-27', 2, 'http://localhost:8080/uploads/f9812d40-eb7a-4d18-b6ba-ef2dcb17bcdb.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `clothing` VALUES (15, '1', '1', '1', '夏季', '全新', '1', '2026-03-06', NULL, 2, 'http://localhost:8080/uploads/267bc2bd-82fa-41a1-a07f-1e35edc023d3.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for image
-- ----------------------------
DROP TABLE IF EXISTS `image`;
CREATE TABLE `image`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `size` bigint(20) NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `upload_time` datetime NOT NULL,
  `entity_id` bigint(20) NULL DEFAULT NULL,
  `entity_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_entity`(`entity_id` ASC, `entity_type` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_upload_time`(`upload_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of image
-- ----------------------------
INSERT INTO `image` VALUES (1, '31470b20-fc3c-4ede-b07d-54e969391396.png', 'e:\\workbase\\clothing\\uploads\\31470b20-fc3c-4ede-b07d-54e969391396.png', 'http://localhost:8080/uploads/31470b20-fc3c-4ede-b07d-54e969391396.png', 479, 'image/png', '2026-02-26 08:48:14', 3, 'clothing', 2);
INSERT INTO `image` VALUES (2, '23d92dcb-1d03-4cd2-ad20-46f058cdd050.png', 'e:\\workbase\\clothing\\uploads\\23d92dcb-1d03-4cd2-ad20-46f058cdd050.png', 'http://localhost:8080/uploads/23d92dcb-1d03-4cd2-ad20-46f058cdd050.png', 168772, 'image/png', '2026-02-26 08:49:21', 4, 'clothing', 2);
INSERT INTO `image` VALUES (3, 'f9812d40-eb7a-4d18-b6ba-ef2dcb17bcdb.png', 'e:\\workbase\\clothing\\uploads\\f9812d40-eb7a-4d18-b6ba-ef2dcb17bcdb.png', 'http://localhost:8080/uploads/f9812d40-eb7a-4d18-b6ba-ef2dcb17bcdb.png', 124763, 'image/png', '2026-02-26 10:19:15', 5, 'clothing', 2);
INSERT INTO `image` VALUES (4, '267bc2bd-82fa-41a1-a07f-1e35edc023d3.png', 'C:\\Users\\ASUS\\Desktop\\clothing-frontend\\clothing\\uploads\\267bc2bd-82fa-41a1-a07f-1e35edc023d3.png', 'http://localhost:8080/uploads/267bc2bd-82fa-41a1-a07f-1e35edc023d3.png', 168772, 'image/png', '2026-03-07 08:12:19', 15, 'clothing', 2);

-- ----------------------------
-- Table structure for location
-- ----------------------------
DROP TABLE IF EXISTS `location`;
CREATE TABLE `location`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of location
-- ----------------------------
INSERT INTO `location` VALUES (1, '1', 1);
INSERT INTO `location` VALUES (2, '1', 2);
INSERT INTO `location` VALUES (3, '3', 2);
INSERT INTO `location` VALUES (4, '2', 2);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` date NULL DEFAULT NULL,
  `receiver_id` bigint(20) NULL DEFAULT NULL,
  `sender_id` bigint(20) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, '1', '2026-03-07', 2, 2, 'read');
INSERT INTO `message` VALUES (2, '2', '2026-03-07', 2, 2, 'read');
INSERT INTO `message` VALUES (3, '1', '2026-03-07', 2, 4, 'read');
INSERT INTO `message` VALUES (4, '2', '2026-03-07', 2, 4, 'read');
INSERT INTO `message` VALUES (5, '31321312', '2026-03-07', 4, 2, 'sent');

-- ----------------------------
-- Table structure for recycle_item
-- ----------------------------
DROP TABLE IF EXISTS `recycle_item`;
CREATE TABLE `recycle_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `clothing_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `size` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `brand` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `material` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `quality_rating` int(11) NULL DEFAULT NULL,
  `recycle_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` double NULL DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '待审核',
  `latitude` double NULL DEFAULT NULL,
  `longitude` double NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `image_urls` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `clothing_condition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recycle_item
-- ----------------------------
INSERT INTO `recycle_item` VALUES (1, 2, '衬衫', '1', '1', '1', '1', 5, '捐赠', NULL, '待审核', 29.138, 119.6337, '纬度: 29.138, 经度: 119.6337', '9f25b5a7-c24f-4f88-92ec-c0a82a64fc42.png', '2026-02-27 10:16:46.977685', '2026-03-07 06:59:00.959537', '几乎全新');
INSERT INTO `recycle_item` VALUES (2, 2, '衬衫', '1', '1', '1', '1', 5, '捐赠', 1, '待审核', 36.934605, 118.557289, '纬度: 36.934605, 经度: 118.557289', 'ed70d051-43d8-4e41-9a65-b87ae88c2249.png', '2026-02-27 10:17:16.205601', '2026-02-27 10:17:16.205601', '几乎全新');
INSERT INTO `recycle_item` VALUES (3, 2, '裤子', '1', '1', '1', '1', 3, '捐赠', 1, '待审核', 36.934605, 118.557289, '纬度: 36.934605, 经度: 118.557289', '64c95276-777b-44f8-9d72-b956cd951345.png', '2026-02-27 10:20:01.418233', '2026-02-27 10:20:01.418233', '几乎全新');
INSERT INTO `recycle_item` VALUES (4, 2, '裤子', '1', '1', '1', '1', 5, '捐赠', NULL, '待审核', 29.138, 119.6337, '纬度: 29.138, 经度: 119.6337', '5a1f1f83-24b2-4a4e-bd2d-f987cba08c50.png', '2026-03-07 08:00:33.145181', '2026-03-07 08:00:33.145181', '全新');

-- ----------------------------
-- Table structure for trade
-- ----------------------------
DROP TABLE IF EXISTS `trade`;
CREATE TABLE `trade`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `clothing_id` bigint(20) NOT NULL,
  `buyer_id` bigint(20) NOT NULL,
  `seller_id` bigint(20) NOT NULL,
  `trade_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `price` decimal(38, 2) NOT NULL,
  `rental_start_date` datetime(6) NULL DEFAULT NULL,
  `rental_end_date` datetime(6) NULL DEFAULT NULL,
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_trade_clothing`(`clothing_id` ASC) USING BTREE,
  INDEX `idx_trade_buyer`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_trade_seller`(`seller_id` ASC) USING BTREE,
  INDEX `idx_trade_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trade
-- ----------------------------
INSERT INTO `trade` VALUES (1, 5, 2, 1, 'purchase', 1.00, NULL, NULL, 'alipay', 'pending', '2026-03-07 08:57:15', '2026-03-07 08:57:15');
INSERT INTO `trade` VALUES (2, 7, 2, 1, 'purchase', 0.00, NULL, NULL, 'wechat', 'pending', '2026-03-07 09:20:23', '2026-03-07 09:20:23');
INSERT INTO `trade` VALUES (3, 6, 2, 1, 'purchase', 0.00, NULL, NULL, 'creditcard', 'pending', '2026-03-07 09:20:34', '2026-03-07 09:20:34');
INSERT INTO `trade` VALUES (4, 4, 2, 1, 'rental', 87380471.00, '2026-03-20 16:00:00.000000', '2026-04-29 16:00:00.000000', 'alipay', 'pending', '2026-03-07 09:50:28', '2026-03-07 09:50:28');
INSERT INTO `trade` VALUES (5, 2, 2, 1, 'purchase', 1.00, NULL, NULL, 'alipay', 'pending', '2026-03-07 09:50:42', '2026-03-07 09:50:42');

-- ----------------------------
-- Table structure for trade_clothing
-- ----------------------------
DROP TABLE IF EXISTS `trade_clothing`;
CREATE TABLE `trade_clothing`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `clothing_condition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` date NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `image_urls` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` double NULL DEFAULT NULL,
  `rental_price` double NULL DEFAULT NULL,
  `size` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `updated_at` date NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `rejection_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `review_date` date NULL DEFAULT NULL,
  `review_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reviewer_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trade_clothing
-- ----------------------------
INSERT INTO `trade_clothing` VALUES (2, '裤子', '全新', '2026-03-07', '1', '4df21773-e7ca-4910-be2e-288988f21af4.png', 1, 1, '1', 'available', '1', '2026-03-07', 2, NULL, NULL, NULL, NULL);
INSERT INTO `trade_clothing` VALUES (3, '上衣', '几乎全新', '2026-03-07', '1', NULL, 0, 1312312, '1', 'available', '衬衫 - 几乎全新', '2026-03-07', 2, NULL, NULL, NULL, NULL);
INSERT INTO `trade_clothing` VALUES (5, '上衣', '几乎全新', '2026-03-07', '1', 'ed70d051-43d8-4e41-9a65-b87ae88c2249.png', 1, NULL, '1', 'available', '衬衫 - 几乎全新', '2026-03-07', 2, NULL, NULL, NULL, NULL);
INSERT INTO `trade_clothing` VALUES (8, '上衣', '几乎全新', '2026-03-08', '1', '9f25b5a7-c24f-4f88-92ec-c0a82a64fc42.png', 0, 1, '1', 'available', '衬衫 - 几乎全新', '2026-03-08', 2, NULL, NULL, 'pending', NULL);
INSERT INTO `trade_clothing` VALUES (9, '裤子', '全新', '2026-03-08', '1', '07638203-558a-487a-82c2-5dc0e7bbb34f.png', 1, 1, '1', 'available', '1', '2026-03-08', 2, NULL, NULL, 'pending', NULL);
INSERT INTO `trade_clothing` VALUES (10, '上衣', '几乎全新', '2026-03-08', '1', '9f25b5a7-c24f-4f88-92ec-c0a82a64fc42.png', 0, 1, '1', 'available', '衬衫 - 几乎全新', '2026-03-08', 2, NULL, NULL, 'pending', NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '123', '123', 'USER');
INSERT INTO `user` VALUES (2, '1234', '123456', 'USER');
INSERT INTO `user` VALUES (3, 'admin', '123456', 'ADMIN');
INSERT INTO `user` VALUES (4, '12345', '123456', 'USER');

SET FOREIGN_KEY_CHECKS = 1;
