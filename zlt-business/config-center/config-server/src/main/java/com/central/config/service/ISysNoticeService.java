package com.central.config.service;

import com.central.common.model.Result;
import com.central.common.service.ISuperService;
import com.central.config.model.SysNotice;
import com.central.config.model.co.FindNoticeCo;
import com.central.config.model.co.UpdateNoticeCo;

import java.util.List;
import java.util.Map;


public interface ISysNoticeService extends ISuperService<SysNotice> {

    /**
     * 查询公告列表
     * @param params
     * @return
     */
    List<SysNotice> findNoticeList(FindNoticeCo params) ;


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
    Result updateEnabled(UpdateNoticeCo params);

    Result saveOrUpdateUser(SysNotice sysNotice) throws Exception;

    /**
     * 异步推送消息到前端
     */
    void syncPushNoticeToWebApp();


    List<SysNotice> getNoticeList();
}
