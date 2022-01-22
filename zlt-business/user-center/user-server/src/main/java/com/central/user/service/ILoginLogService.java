package com.central.user.service;

import com.central.common.model.LoginLog;
import com.central.common.model.PageResult;
import com.central.common.service.ISuperService;

import java.util.Map;

public interface ILoginLogService extends ISuperService<LoginLog> {
	/**
	 * 查询日志
	 * @return
	 */
	PageResult queryList(Map<String, Object> map);



}
