package com.central.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.central.activity.mapper.FileServiceMapper;
import com.central.activity.model.FileCustom;
import com.central.activity.service.IFileService;
import com.central.common.service.impl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FileServiceImpl extends SuperServiceImpl<FileServiceMapper, FileCustom> implements IFileService {

    @Autowired
    FileServiceMapper fileServiceMapper;
    /**
     * 获取某文件类型未备份的文件
     * @param fileType 文件类型
     * @param count 获取条数
     * @return
     */
    public List<FileCustom> fetchUnBackupFiles(String fileType, Integer count){
        QueryWrapper queryWrapper = new QueryWrapper<FileCustom>().eq("type", fileType).eq("backed_up", 0);
        queryWrapper.last("limit 0,"+count);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 备份文件
     * @param files 要备份的文件
     */
    public void backupFiles(List<FileCustom> files){
        for(FileCustom fileCustom:files){
            FileCustom f =  new FileCustom();
            f.setId(fileCustom.getId());
            f.setBackedUp(false);
            baseMapper.updateById(f);
            System.out.println(String.format("线程 %d | 已备份文件:%s  文件类型:%s"
                    ,Thread.currentThread().getId()
                    ,fileCustom.getName()
                    ,fileCustom.getType()));
        }

    }
}
