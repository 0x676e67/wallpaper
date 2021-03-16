DROP DATABASE wallpaper;
CREATE DATABASE wallpaper CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE wallpaper;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for index_entity
-- ----------------------------
DROP TABLE IF EXISTS `index_entity`;
CREATE TABLE `index_entity` (
                                `data_id` int(11) DEFAULT NULL,
                                `name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                `type` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL,
                                `index` int(11) DEFAULT NULL,
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3787 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;