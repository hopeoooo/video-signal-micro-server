package com.central.db.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mapper 父类，注意这个类不要让 mp 扫描到！！
 * @author zlt
 */
public interface SuperMapper<T> extends BaseMapper<T> {
    // 这里可以放一些公共的方法

    int insertBatch(@Param("list") List<T> list);

    int updateBatch(@Param("list") List<T> list);
}
