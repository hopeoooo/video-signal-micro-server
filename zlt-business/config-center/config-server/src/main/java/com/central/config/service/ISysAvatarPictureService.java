package com.central.config.service;

import com.central.common.model.SysAvatarPicture;
import com.central.common.service.ISuperService;

import java.util.List;
import java.util.Map;


public interface ISysAvatarPictureService extends ISuperService<SysAvatarPicture> {
    /**
     * 查询头像列表
     * @return
     */
    List<SysAvatarPicture> findAvatarPictureList() ;

   boolean saveAvatarPicture( List<SysAvatarPicture> list);



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
