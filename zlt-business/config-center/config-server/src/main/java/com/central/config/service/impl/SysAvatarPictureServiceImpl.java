package com.central.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.config.mapper.SysAvatarPictureMapper;
import com.central.config.model.SysAvatarPicture;
import com.central.config.service.ISysAvatarPictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@CacheConfig(cacheNames = {"sysAvatarPicture"})
public class SysAvatarPictureServiceImpl extends SuperServiceImpl<SysAvatarPictureMapper, SysAvatarPicture> implements ISysAvatarPictureService {

    @Override
    public List<SysAvatarPicture> findAvatarPictureList() {
        return  baseMapper.selectList( new QueryWrapper<SysAvatarPicture>().orderByAsc("create_time"));
    }

    @Override
    public String avatarPictureInfo() {
       return baseMapper.avatarPictureInfo();
    }

    @Override
    public boolean saveAvatarPicture(List<SysAvatarPicture> list) {
        return super.saveBatch(list);
    }

    @Override
    public SysAvatarPicture selectById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean delAvatarPictureId(Long id) {
        return baseMapper.deleteById(id) > 0;
    }
}