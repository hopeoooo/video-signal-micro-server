package com.central.game.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.SysUser;
import com.central.db.mapper.SuperMapper;
import com.central.game.model.GameLotteryResult;
import com.central.game.model.co.GameLotteryResultBackstageCo;
import com.central.game.model.co.GameLotteryResultCo;
import com.central.user.model.co.SysUserListCo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Mapper
public interface GameLotteryResultMapper extends SuperMapper<GameLotteryResult> {

    List<GameLotteryResult> getLotteryResultList(@Param("p") GameLotteryResultCo co);


    List<GameLotteryResult> findList(Page<GameLotteryResult> page, @Param("p") GameLotteryResultBackstageCo co);
}
