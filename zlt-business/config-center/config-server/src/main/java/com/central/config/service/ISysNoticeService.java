package com.central.config.service;

import com.central.common.model.Result;
import com.central.common.model.SysNotice;
import com.central.common.service.ISuperService;

import java.util.List;
import java.util.Map;


public interface ISysNoticeService extends ISuperService<SysNotice> {

    /**
     * 查询公告列表
     * @param params
     * @return
     */
    List<SysNotice> findNoticeList(Map<String, Object> params) ;


    /**
     * 根据id查询公告
     * @param id
     * @return
     */
    SysNotice selectById(Long id) ;

    /**
     * 删除公告
     * @param id
     * @return
     */
    boolean delNoticeId(Long id);

    /**
     * 状态变更
     * @param params
     * @return
     */
    Result updateEnabled(Map<String, Object> params);

    Result saveOrUpdateUser(SysNotice sysNotice) throws Exception;


}
