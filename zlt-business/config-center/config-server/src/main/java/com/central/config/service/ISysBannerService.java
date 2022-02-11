package com.central.config.service;

import com.central.common.service.ISuperService;
import com.central.config.model.SysBanner;
import com.central.config.model.co.BannerUpdateStateCo;

import java.util.List;
import java.util.Map;


public interface ISysBannerService extends ISuperService<SysBanner> {

    /**
     * 查询轮播图列表
     * @return
     */
    List<SysBanner> findBannerList() ;

    /**
     * 删除公告
     * @param id
     * @return
     */
    boolean delBannerId(Long id);

    /**
     * 状态变更
     * @param params
     * @return
     */
    int updateState(BannerUpdateStateCo params);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    SysBanner selectById(Long id) ;

    /**
     * 新增
     * @param sysBanner
     * @return
     * @throws Exception
     */
    boolean saveOrUpdateUser(SysBanner sysBanner) throws Exception;

    Integer queryTotal(Integer sort);

    List<SysBanner> getBannerList();

    /**
     * 异步推送消息到前端
     */
    void syncPushBannerToWebApp();

}
