------------更新语句
Use `oauth-center`;
update oauth_client_details set additional_information = '{"LOGOUT_NOTIFY_URL_LIST":"http://127.0.0.1:8082/logoutNotify"}'
where client_id = 'webApp';

update oauth_client_details set additional_information = '{"LOGOUT_NOTIFY_URL_LIST":"http://127.0.0.1:8081/logoutNotify"}'
where client_id = 'app';


ALTER TABLE `user-center`.`sys_user`
ADD COLUMN `ga_bind` tinyint(1) NULL COMMENT '谷歌验证码是否绑定1 1：已绑定，其他：未绑定' AFTER `is_auto_bet`,
ADD COLUMN `ga_key` varchar(255) NULL COMMENT '谷歌验证码KEY' AFTER `ga_bind`;