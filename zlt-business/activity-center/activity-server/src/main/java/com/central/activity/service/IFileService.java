package com.central.activity.service;

import com.central.activity.model.FileCustom;
import com.central.common.service.ISuperService;

import java.util.List;

public interface IFileService extends ISuperService<FileCustom> {

    /**
     * 获取某文件类型未备份的文件
     * @param fileType 文件类型
     * @param count 获取条数
     * @return
     */
    public List<FileCustom> fetchUnBackupFiles(String fileType, Integer count);

    /**
     * 备份文件
     * @param files 要备份的文件
     */
    public void backupFiles(List<FileCustom> files);
}
