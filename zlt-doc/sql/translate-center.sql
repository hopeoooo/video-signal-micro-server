DROP TABLE IF EXISTS `i18n_position`;
CREATE TABLE `i18n_position` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL COMMENT '位置名称',
    `type` int(11) NOT NULL DEFAULT 0 COMMENT '0 页面 1 页面的某个位置',
    `pid` bigint(20) NOT NULL DEFAULT -1 COMMENT '默认-1,父级id',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
    UNIQUE INDEX `uq_name`(`name`) USING BTREE COMMENT '位置名称唯一索引',
    PRIMARY KEY(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci COMMENT = '翻译位置表';

INSERT INTO `translate-center`.`i18n_position` (`id`, `name`, `type`, `pid`) VALUES (1, '系统消息', 0, -1);


DROP TABLE IF EXISTS `i18n_info`;
CREATE TABLE `i18n_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `page_id` bigint(20) NOT NULL COMMENT '页面id,position表的type=0的数据',
    `position_id` bigint(20) NULL DEFAULT NULL COMMENT '位置id,position表的type=1的数据',
    `zh_cn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL DEFAULT '' COMMENT '中文-简体',
    `en_us` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL DEFAULT '' COMMENT '英语-美国',
    `khm` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL DEFAULT '' COMMENT '高棉语',
    `th` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL DEFAULT '' COMMENT '泰语',
    `operator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL DEFAULT '' COMMENT '操作人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime NULL DEFAULT NULL COMMENT '操作时间',   
    PRIMARY KEY(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci COMMENT = '国际化字典表';

INSERT INTO `translate-center`.`i18n_info` (`id`, `page_id`, `position_id`, `zh_cn`, `en_us`, `khm`, `th`, `operator`) VALUES (1, 1, NULL, '默认', '', '', '', '');
