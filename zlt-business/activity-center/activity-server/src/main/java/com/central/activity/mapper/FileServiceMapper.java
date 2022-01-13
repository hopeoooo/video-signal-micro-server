package com.central.activity.mapper;

import com.central.activity.model.FileCustom;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单
 */
@Mapper
public interface FileServiceMapper extends SuperMapper<FileCustom> {

}
