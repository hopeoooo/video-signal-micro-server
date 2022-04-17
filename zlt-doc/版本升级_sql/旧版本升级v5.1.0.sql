------------更新语句
Use `oauth-center`;
update oauth_client_details set additional_information = '{"LOGOUT_NOTIFY_URL_LIST":"http://127.0.0.1:8082/logoutNotify"}'
where client_id = 'webApp';

update oauth_client_details set additional_information = '{"LOGOUT_NOTIFY_URL_LIST":"http://127.0.0.1:8081/logoutNotify"}'
where client_id = 'app';


ALTER TABLE `user-center`.`sys_user`
ADD COLUMN `ga_bind` tinyint(1) NULL COMMENT '谷歌验证码是否绑定1 1：已绑定，其他：未绑定' AFTER `is_auto_bet`,
ADD COLUMN `ga_key` varchar(255) NULL COMMENT '谷歌验证码KEY' AFTER `ga_bind`;

ALTER TABLE `user-center`.`sys_user`
ADD COLUMN `verify` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否验证 1：是，其他：否' AFTER `ga_key`;


user `platform-backend-server`;

ALTER TABLE `platform-backend-server`.`sys_user`
DROP INDEX `idx_username`,
ADD UNIQUE INDEX `idx_username`(`username`) USING BTREE;

CREATE TABLE "user_report" (
  "id" bigint(11) NOT NULL,
  "user_id" bigint(20) NOT NULL COMMENT '用户id',
  "statics_times" varchar(255) NOT NULL COMMENT '日期(yyyy-MM-dd)',
  "charge_amount" decimal(24,6) NOT NULL COMMENT '充值金额',
  "withdraw_money" decimal(24,6) NOT NULL COMMENT '提现金额',
  "valid_amount" decimal(24,6) NOT NULL COMMENT '有效投注',
  "win_loss" decimal(24,6) NOT NULL COMMENT '输赢',
  PRIMARY KEY ("id"),
  KEY "user_id_index" ("user_id") USING BTREE,
  KEY "statics_times_index" ("statics_times") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;