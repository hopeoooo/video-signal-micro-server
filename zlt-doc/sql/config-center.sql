
CREATE DATABASE IF NOT EXISTS `config-center` DEFAULT CHARACTER SET = utf8;
Use `config-center`;

-- ----------------------------
-- Table structure for download_station
-- ----------------------------
DROP TABLE IF EXISTS `download_station`;
CREATE TABLE `download_station`  (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                     `create_time` datetime(0) NULL DEFAULT NULL,
                                     `update_time` datetime(0) NULL DEFAULT NULL,
                                     `download_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                     `is_forced` int(11) NULL DEFAULT NULL,
                                     `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                     `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                     `terminal_type` int(11) NULL DEFAULT NULL,
                                     `version_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                     `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                     `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_avatar_picture
-- ----------------------------
DROP TABLE IF EXISTS `sys_avatar_picture`;
CREATE TABLE `sys_avatar_picture`  (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片地址',
                                       `file_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '图片关联id',
                                       `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `create_time` datetime(0) NULL DEFAULT NULL,
                                       `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `update_time` datetime(0) NULL DEFAULT NULL,
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_banner
-- ----------------------------
DROP TABLE IF EXISTS `sys_banner`;
CREATE TABLE `sys_banner`  (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `create_time` datetime(0) NULL DEFAULT NULL,
                               `update_time` datetime(0) NULL DEFAULT NULL,
                               `h5_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'h5图片url',
                               `h5_file_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '图片关联id',
                               `web_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'web图片url',
                               `web_file_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '图片关联id',
                               `state` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态(0:停用,1:启用)',
                               `sort` int(5) NOT NULL COMMENT '排序',
                               `link_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '链接url',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `create_time` datetime(0) NULL DEFAULT NULL,
                               `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
                               `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL COMMENT '公告内容',
                               `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '备注',
                               `type` int(10) NOT NULL DEFAULT 1 COMMENT '类型(1:一般,2:维护,3:系统)',
                               `state` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态(0:停用,1:启用)',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_platform_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_platform_config`;
CREATE TABLE `sys_platform_config`  (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `tourist_amount` decimal(15, 6) NULL DEFAULT NULL COMMENT '游客携带金额',
                                        `tourist_single_max_bet` decimal(15, 6) NULL DEFAULT NULL COMMENT '游客单笔最大投注',
                                        `money_symbol` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '金钱符号',
                                        `website_icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '网站icon图',
                                        `log_image_url_app` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'logo(App)',
                                        `log_image_url_pc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'logo(pc)',
                                        `login_register_log_image_url_app` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'logo(App登陆注册)',
                                        `app_download_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'app下载地址',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
