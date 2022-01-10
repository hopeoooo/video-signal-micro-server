CREATE DATABASE IF NOT EXISTS `platform-backend-server` DEFAULT CHARACTER SET = utf8;
Use `platform-backend-server`;

DROP TABLE IF EXISTS `online_user`;
CREATE TABLE `online_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `statics_times` varchar(255) NULL,
  `statics_day` varchar(255) NULL,
  `online_num` int(255) NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);
