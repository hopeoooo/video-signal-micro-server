package com.central.config.service;

import com.central.common.model.SysAvatarPicture;
import com.central.common.model.SysBanner;
import com.central.common.service.ISuperService;

import java.util.List;
import java.util.Map;


public interface ISysAvatarPictureService extends ISuperService<SysAvatarPicture> {
    /**
     * 查询头像列表
     * @return
     */
    List<SysAvatarPicture> findAvatarPictureList() ;

}
