package com.central.config.mapper;

import com.central.config.model.SysAvatarPicture;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysAvatarPictureMapper extends SuperMapper<SysAvatarPicture> {

    String avatarPictureInfo();
}
