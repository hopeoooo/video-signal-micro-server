package com.central.user.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.SysUserMoney;
import com.central.db.mapper.SuperMapper;
import com.central.user.model.RoomFollowList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户钱包表
 * 
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Mapper
public interface RoomFollowListMapper extends SuperMapper<RoomFollowList> {
}
