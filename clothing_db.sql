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

 Date: 26/04/2026 17:30:02
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
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of clothing
-- ----------------------------
INSERT INTO `clothing` VALUES (3, '2', '4', '1', '春季', '良好', '1', '2026-02-12', '2026-02-27', 2, 'http://localhost:8080/uploads/83aa4f2f-3849-4e21-a802-54aeeb4dec13.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `clothing` VALUES (4, '1', '1', '1', '秋季', '较差', '卧室-鞋柜', '2026-02-05', '2026-02-28', 2, 'http://localhost:8080/uploads/23d92dcb-1d03-4cd2-ad20-46f058cdd050.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `clothing` VALUES (5, '1', '1', '1', '春季', '良好', '3', '2026-02-03', '2026-02-27', 2, 'http://localhost:8080/uploads/f9812d40-eb7a-4d18-b6ba-ef2dcb17bcdb.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `clothing` VALUES (15, '1', '1', '1', '夏季', '全新', '1', '2026-03-06', NULL, 2, 'http://localhost:8080/uploads/267bc2bd-82fa-41a1-a07f-1e35edc023d3.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `clothing` VALUES (18, '1', '2', '3', '春季', '良好', '1', '2026-03-12', '2026-03-20', 2, 'http://localhost:8080/uploads/45226588-cc9a-442f-8a34-7a9e455a4173.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `clothing` VALUES (20, '1', '1', '1', '夏季', '较差', '1', '2025-04-08', '2025-04-06', 2, 'http://localhost:8080/uploads/8a6d62c3-c9d3-4b19-925e-8f2ae3f76cec.png', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `clothing` VALUES (21, '运动鞋', '鞋', '44', '春季', '良好', '卧室-鞋柜', '2026-04-05', '2026-04-06', 2, 'http://localhost:8080/uploads/a19aa51b-4756-4e98-8cba-ea931b59c92c.jpg', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for idle_clothing_alert
-- ----------------------------
DROP TABLE IF EXISTS `idle_clothing_alert`;
CREATE TABLE `idle_clothing_alert`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alert_date` date NULL DEFAULT NULL,
  `clothing_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `clothing_id` bigint(20) NULL DEFAULT NULL,
  `clothing_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `exception_rule` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `idle_days` int(11) NULL DEFAULT NULL,
  `last_wear_date` date NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of idle_clothing_alert
-- ----------------------------
INSERT INTO `idle_clothing_alert` VALUES (1, '2026-04-09', '上衣', 19, '测试衣物', '无', 365, '2025-04-10', 'pending', 2);
INSERT INTO `idle_clothing_alert` VALUES (2, '2026-04-09', '未分类', 20, '1', '夏季', 368, '2025-04-06', 'pending', 2);

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
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of image
-- ----------------------------
INSERT INTO `image` VALUES (1, '31470b20-fc3c-4ede-b07d-54e969391396.png', 'e:\\workbase\\clothing\\uploads\\31470b20-fc3c-4ede-b07d-54e969391396.png', 'http://localhost:8080/uploads/31470b20-fc3c-4ede-b07d-54e969391396.png', 479, 'image/png', '2026-02-26 08:48:14', 3, 'clothing', 2);
INSERT INTO `image` VALUES (2, '23d92dcb-1d03-4cd2-ad20-46f058cdd050.png', 'e:\\workbase\\clothing\\uploads\\23d92dcb-1d03-4cd2-ad20-46f058cdd050.png', 'http://localhost:8080/uploads/23d92dcb-1d03-4cd2-ad20-46f058cdd050.png', 168772, 'image/png', '2026-02-26 08:49:21', 4, 'clothing', 2);
INSERT INTO `image` VALUES (3, 'f9812d40-eb7a-4d18-b6ba-ef2dcb17bcdb.png', 'e:\\workbase\\clothing\\uploads\\f9812d40-eb7a-4d18-b6ba-ef2dcb17bcdb.png', 'http://localhost:8080/uploads/f9812d40-eb7a-4d18-b6ba-ef2dcb17bcdb.png', 124763, 'image/png', '2026-02-26 10:19:15', 5, 'clothing', 2);
INSERT INTO `image` VALUES (4, '267bc2bd-82fa-41a1-a07f-1e35edc023d3.png', 'C:\\Users\\ASUS\\Desktop\\clothing-frontend\\clothing\\uploads\\267bc2bd-82fa-41a1-a07f-1e35edc023d3.png', 'http://localhost:8080/uploads/267bc2bd-82fa-41a1-a07f-1e35edc023d3.png', 168772, 'image/png', '2026-03-07 08:12:19', 15, 'clothing', 2);
INSERT INTO `image` VALUES (5, '45226588-cc9a-442f-8a34-7a9e455a4173.png', 'C:\\Users\\ASUS\\Desktop\\clothing-frontend\\clothing\\uploads\\45226588-cc9a-442f-8a34-7a9e455a4173.png', 'http://localhost:8080/uploads/45226588-cc9a-442f-8a34-7a9e455a4173.png', 168772, 'image/png', '2026-03-12 01:03:00', 18, 'clothing', 2);
INSERT INTO `image` VALUES (6, '3b29b7da-e452-4481-9c24-3859ea69713d.png', 'C:\\Users\\ASUS\\Desktop\\clothing-frontend\\clothing\\uploads\\3b29b7da-e452-4481-9c24-3859ea69713d.png', 'http://localhost:8080/uploads/3b29b7da-e452-4481-9c24-3859ea69713d.png', 168772, 'image/png', '2026-04-09 04:54:17', 19, 'clothing', 2);
INSERT INTO `image` VALUES (7, '8a6d62c3-c9d3-4b19-925e-8f2ae3f76cec.png', 'C:\\Users\\ASUS\\Desktop\\clothing-frontend\\clothing\\uploads\\8a6d62c3-c9d3-4b19-925e-8f2ae3f76cec.png', 'http://localhost:8080/uploads/8a6d62c3-c9d3-4b19-925e-8f2ae3f76cec.png', 238345, 'image/png', '2026-04-09 05:26:08', 20, 'clothing', 2);
INSERT INTO `image` VALUES (8, 'a19aa51b-4756-4e98-8cba-ea931b59c92c.jpg', 'C:\\Users\\ASUS\\Desktop\\clothing-frontend\\clothing\\uploads\\a19aa51b-4756-4e98-8cba-ea931b59c92c.jpg', 'http://localhost:8080/uploads/a19aa51b-4756-4e98-8cba-ea931b59c92c.jpg', 138890, 'image/jpeg', '2026-04-12 08:23:48', 21, 'clothing', 2);
INSERT INTO `image` VALUES (9, '58b65e30-8b23-4275-b7aa-55dcd216b8ad.jpg', 'C:\\Users\\ASUS\\Desktop\\clothing-frontend\\clothing\\uploads\\58b65e30-8b23-4275-b7aa-55dcd216b8ad.jpg', 'http://localhost:8080/uploads/58b65e30-8b23-4275-b7aa-55dcd216b8ad.jpg', 138890, 'image/jpeg', '2026-04-26 05:28:11', 3, 'clothing', 2);
INSERT INTO `image` VALUES (10, '83aa4f2f-3849-4e21-a802-54aeeb4dec13.png', 'C:\\Users\\ASUS\\Desktop\\clothing-frontend\\clothing\\uploads\\83aa4f2f-3849-4e21-a802-54aeeb4dec13.png', 'http://localhost:8080/uploads/83aa4f2f-3849-4e21-a802-54aeeb4dec13.png', 168772, 'image/png', '2026-04-26 05:34:12', 3, 'clothing', 2);

-- ----------------------------
-- Table structure for location
-- ----------------------------
DROP TABLE IF EXISTS `location`;
CREATE TABLE `location`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of location
-- ----------------------------
INSERT INTO `location` VALUES (1, '1', 1);
INSERT INTO `location` VALUES (2, '1', 2);
INSERT INTO `location` VALUES (3, '3', 2);
INSERT INTO `location` VALUES (4, '2', 2);
INSERT INTO `location` VALUES (5, '4', 2);
INSERT INTO `location` VALUES (6, '卧室-鞋柜', 2);

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
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, '1', '2026-03-07', 2, 2, 'read');
INSERT INTO `message` VALUES (2, '2', '2026-03-07', 2, 2, 'read');
INSERT INTO `message` VALUES (3, '1', '2026-03-07', 2, 4, 'read');
INSERT INTO `message` VALUES (4, '2', '2026-03-07', 2, 4, 'read');
INSERT INTO `message` VALUES (5, '31321312', '2026-03-07', 4, 2, 'read');
INSERT INTO `message` VALUES (6, '12312312', '2026-03-12', 2, 4, 'read');
INSERT INTO `message` VALUES (7, 'nihao', '2026-03-12', 2, 4, 'read');
INSERT INTO `message` VALUES (8, '你好', '2026-04-09', 4, 2, 'read');
INSERT INTO `message` VALUES (9, '你不好', '2026-04-09', 4, 2, 'read');
INSERT INTO `message` VALUES (10, '1', '2026-04-09', 2, 2, 'read');
INSERT INTO `message` VALUES (11, '13', '2026-04-09', 2, 2, 'read');
INSERT INTO `message` VALUES (12, '1', '2026-04-09', 2, 2, 'read');
INSERT INTO `message` VALUES (13, '大', '2026-04-09', 2, 2, 'read');
INSERT INTO `message` VALUES (14, ' 啊', '2026-04-09', 4, 2, 'read');
INSERT INTO `message` VALUES (15, '你好', '2026-04-09', 4, 2, 'read');
INSERT INTO `message` VALUES (16, '你好', '2026-04-09', 4, 2, 'read');
INSERT INTO `message` VALUES (17, '你好', '2026-04-09', 2, 2, 'read');
INSERT INTO `message` VALUES (18, '1', '2026-04-10', 2, 2, 'read');
INSERT INTO `message` VALUES (19, '1', '2026-04-10', 2, 2, 'read');
INSERT INTO `message` VALUES (20, '1', '2026-04-10', 2, 2, 'read');
INSERT INTO `message` VALUES (21, '2', '2026-04-10', 4, 2, 'read');
INSERT INTO `message` VALUES (22, '1', '2026-04-10', 4, 2, 'read');
INSERT INTO `message` VALUES (23, '1', '2026-04-10', 2, 2, 'read');
INSERT INTO `message` VALUES (24, '大', '2026-04-10', 2, 2, 'read');
INSERT INTO `message` VALUES (25, '1', '2026-04-10', 2, 2, 'read');
INSERT INTO `message` VALUES (26, '1', '2026-04-10', 2, 2, 'read');
INSERT INTO `message` VALUES (27, '1', '2026-04-10', 4, 2, 'read');
INSERT INTO `message` VALUES (28, 'nihao', '2026-04-12', 4, 2, 'read');
INSERT INTO `message` VALUES (29, '\'', '2026-04-12', 4, 2, 'read');
INSERT INTO `message` VALUES (30, '\\', '2026-04-12', 4, 2, 'read');
INSERT INTO `message` VALUES (31, '1', '2026-04-25', 2, 4, 'read');

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
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recycle_item
-- ----------------------------
INSERT INTO `recycle_item` VALUES (1, 2, '衬衫', '2', '1', '1', '1', 5, '捐赠', NULL, '已批准', 29.138, 119.6337, '纬度: 29.138, 经度: 119.6337', '9f25b5a7-c24f-4f88-92ec-c0a82a64fc42.png', '2026-02-27 10:16:46.977685', '2026-04-09 04:38:40.222000', '几乎全新');
INSERT INTO `recycle_item` VALUES (7, 2, '衬衫', '1', '23123', '123', '1231', 5, '捐赠', NULL, '待审核', NULL, NULL, '1', 'e4eee4d1-13fd-4ef1-ad29-6c09be202002.png', '2026-04-09 03:25:18.649866', '2026-04-09 03:25:18.649866', '全新');
INSERT INTO `recycle_item` VALUES (8, 2, '其他', '44', '', '', '', 5, '回收', 200, '已批准', NULL, NULL, '浙江省金华市婺城区浙江师范大学', '598ae11b-d7b6-4964-8c1c-7c79fdab6c94.jpg', '2026-04-12 08:26:25.387947', '2026-04-12 08:28:17.018094', '几乎全新');

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
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `district` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `recipient` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_trade_clothing`(`clothing_id` ASC) USING BTREE,
  INDEX `idx_trade_buyer`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_trade_seller`(`seller_id` ASC) USING BTREE,
  INDEX `idx_trade_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trade
-- ----------------------------
INSERT INTO `trade` VALUES (11, 12, 4, 2, 'purchase', 0.00, NULL, NULL, 'alipay', 'shipped', '2026-04-10 08:13:35', '2026-04-25 13:01:23', '1', '1', '1', '1', '1', '1');
INSERT INTO `trade` VALUES (12, 12, 4, 2, 'purchase', 0.00, NULL, NULL, 'wechat', 'paid', '2026-04-10 08:13:58', '2026-04-25 12:58:58', '123', '123', '1231', '21312', '213', '2131');
INSERT INTO `trade` VALUES (13, 11, 4, 2, 'purchase', 1.00, NULL, NULL, 'alipay', 'pending', '2026-04-10 08:45:59', '2026-04-10 08:45:59', '3', '6', '5', '2', '4', '12');
INSERT INTO `trade` VALUES (14, 15, 4, 2, 'purchase', 200.00, NULL, NULL, 'alipay', 'pending', '2026-04-12 08:36:18', '2026-04-12 08:36:18', '金华市', '浙江师范大学', '婺城区', '1234536', '浙江省', 'yy');
INSERT INTO `trade` VALUES (15, 12, 4, 2, 'purchase', 0.00, NULL, NULL, 'alipay', 'pending', '2026-04-25 12:16:50', '2026-04-25 12:16:50', '1', '1', '11', '1', '1', '1');
INSERT INTO `trade` VALUES (16, 16, 2, 4, 'purchase', 1.00, NULL, NULL, 'alipay', 'paid', '2026-04-25 12:40:48', '2026-04-25 12:48:41', '1', '1', '1', '1', '1', '1');
INSERT INTO `trade` VALUES (17, 17, 4, 2, 'rental', 7392.00, '2026-04-23 16:00:00.000000', '2026-05-24 16:00:00.000000', 'alipay', 'pending', '2026-04-25 13:06:01', '2026-04-25 13:06:01', '321', '312', '312', '231', '312', '1');

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
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trade_clothing
-- ----------------------------
INSERT INTO `trade_clothing` VALUES (11, '裤子', '几乎全新', '2026-03-12', '1', 'c772c647-4e27-4602-b4fe-98d4ca2cefd9.png', 1, NULL, '1', 'sold', '裤子 - 几乎全新', '2026-04-10', 2, NULL, '2026-03-12', 'approved', 3);
INSERT INTO `trade_clothing` VALUES (12, '上衣', '全新', '2026-04-09', '1231', 'e4eee4d1-13fd-4ef1-ad29-6c09be202002.png', 0, NULL, '1', 'sold', '衬衫 - 全新', '2026-04-25', 2, NULL, '2026-04-09', 'approved', 3);
INSERT INTO `trade_clothing` VALUES (13, '上衣', '全新', '2026-04-09', '1231', 'e4eee4d1-13fd-4ef1-ad29-6c09be202002.png', 0, NULL, '1', 'available', '衬衫 - 全新', '2026-04-25', 2, NULL, '2026-04-25', 'approved', 3);
INSERT INTO `trade_clothing` VALUES (14, '上衣', '几乎全新', '2026-04-10', '1', '9f25b5a7-c24f-4f88-92ec-c0a82a64fc42.png', 0, NULL, '2', 'available', '衬衫 - 几乎全新', '2026-04-10', 2, NULL, NULL, 'pending', NULL);
INSERT INTO `trade_clothing` VALUES (15, '其他', '几乎全新', '2026-04-12', '', '598ae11b-d7b6-4964-8c1c-7c79fdab6c94.jpg', 200, 20, '44', 'sold', '其他 - 几乎全新', '2026-04-12', 2, NULL, '2026-04-12', 'approved', 3);
INSERT INTO `trade_clothing` VALUES (16, '上衣', '全新', '2026-04-25', '1', '36cb4fe2-2f25-4d1f-ba57-e997f15e45c4.jpg', 1, 1, '1', 'sold', '1', '2026-04-25', 4, NULL, '2026-04-25', 'approved', 3);
INSERT INTO `trade_clothing` VALUES (17, '鞋帽', '近似全新', '2026-04-25', '31', 'ad67826c-99c0-43ff-bc57-12fe469d4817.png', 122, 231, '32', 'sold', '1234', '2026-04-25', 2, NULL, '2026-04-25', 'approved', 3);

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
