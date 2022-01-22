package com.central.user.service;

import com.central.common.model.LoginLog;
import com.central.common.model.PageResult2;
import com.central.common.service.ISuperService;

import java.util.Map;

public interface ILoginLogService extends ISuperService<LoginLog> {
	/**
	 * 查询日志
	 * @return
	 */
	PageResult2 queryList(Map<String, Object> map);



}
