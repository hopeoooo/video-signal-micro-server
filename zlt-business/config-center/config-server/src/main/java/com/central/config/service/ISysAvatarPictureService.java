package com.central.config.service;

import com.central.common.service.ISuperService;
import com.central.config.model.SysAvatarPicture;

import java.util.List;


public interface ISysAvatarPictureService extends ISuperService<SysAvatarPicture> {
    /**
     * 查询头像列表
     * @return
     */
    List<SysAvatarPicture> findAvatarPictureList() ;

    boolean saveAvatarPicture( List<SysAvatarPicture> list);


    /**
     * 查询单个头像
     * @return
     */
    String avatarPictureInfo() ;
    /**
     * 根据id查询
     * @param id
     * @return
     */
    SysAvatarPicture selectById(Long id) ;

    /**
     * 删除头像
     * @param id
     * @return
     */
    boolean delAvatarPictureId(Long id);


}
