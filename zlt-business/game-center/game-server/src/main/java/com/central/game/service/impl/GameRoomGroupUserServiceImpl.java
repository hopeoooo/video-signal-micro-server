package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.model.CodeEnum;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.mapper.GameRoomGroupUserMapper;
import com.central.game.model.GameRoomGroup;
import com.central.game.model.GameRoomGroupUser;
import com.central.game.model.vo.GameRoomGroupUserVo;
import com.central.game.service.IGameRoomGroupService;
import com.central.game.service.IGameRoomGroupUserService;
import com.central.game.service.IPushGameDataToClientService;
import com.central.user.feign.UserService;
import com.central.user.model.vo.SysUserInfoMoneyVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GameRoomGroupUserServiceImpl extends SuperServiceImpl<GameRoomGroupUserMapper, GameRoomGroupUser> implements IGameRoomGroupUserService {

    @Autowired
    private GameRoomGroupUserMapper gameRoomGroupUserMapper;
    @Autowired
    private IGameRoomGroupService gameRoomGroupService;
    @Autowired
    private UserService userService;
    @Autowired
    private IPushGameDataToClientService pushGameDataToClientService;

    @Override
    public List<Long> getNotFullGroup(Long gameId, String tableNum) {
        return gameRoomGroupUserMapper.getNotFullGroup(gameId, tableNum);
    }

    @Override
    public GameRoomGroupUser checkExist(Long gameId, String tableNum, Long userId) {
        return gameRoomGroupUserMapper.checkExist(gameId, tableNum, userId);
    }

    @Override
    public void addGroup(Long gameId, String tableNum, SysUser sysUser) {
        //先查询是否已存在
        GameRoomGroupUser user = gameRoomGroupUserMapper.checkExist(gameId, tableNum, sysUser.getId());
        if (user != null) {
            return;
        }
        //查询未满的分组
        List<Long> groupList = gameRoomGroupUserMapper.getNotFullGroup(gameId, tableNum);
        Long groupId = null;
        if (CollectionUtils.isEmpty(groupList)) {
            GameRoomGroup gameRoomGroup = new GameRoomGroup();
            gameRoomGroup.setGameId(gameId);
            gameRoomGroup.setTableNum(tableNum);
            gameRoomGroupService.save(gameRoomGroup);
            groupId = gameRoomGroup.getId();
        } else {
            groupId = groupList.get(0);
        }
        GameRoomGroupUser gameRoomGroupUser = new GameRoomGroupUser();
        gameRoomGroupUser.setUserId(sysUser.getId());
        gameRoomGroupUser.setUserName(sysUser.getUsername());
        gameRoomGroupUser.setGroupId(groupId);
        gameRoomGroupUserMapper.insert(gameRoomGroupUser);

        List<Long> userIdList = new ArrayList<>();
        userIdList.add(sysUser.getId());
        Result<List<SysUserInfoMoneyVo>> result = userService.findListByUserIdList(userIdList);
        if (result.getResp_code() == CodeEnum.SUCCESS.getCode()) {
            GameRoomGroupUserVo vo = new GameRoomGroupUserVo();
            vo.setGameId(gameId);
            vo.setTableNum(tableNum);
            if (!CollectionUtils.isEmpty(result.getDatas())) {
                SysUserInfoMoneyVo userInfoMoneyVo = result.getDatas().get(0);
                BeanUtils.copyProperties(userInfoMoneyVo, vo);
                vo.setUserName(sysUser.getUsername());
                pushGameDataToClientService.syncTableNumGroup(vo);
            }
        }
    }

    @Override
    public List<GameRoomGroupUserVo> getTableNumGroupList(Long gameId, String tableNum, SysUser sysUser) {
        //先查询是否已存在
        List<GameRoomGroupUserVo> list = new ArrayList<>();
        GameRoomGroupUser user = gameRoomGroupUserMapper.checkExist(gameId, tableNum, sysUser.getId());
        if (user == null) {
            return list;
        }
        LambdaQueryWrapper<GameRoomGroupUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRoomGroupUser::getGroupId, user.getGroupId());
        List<GameRoomGroupUser> userList = gameRoomGroupUserMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(userList)) {
            return list;
        }
        List<Long> userIdList = new ArrayList<>();
        for (GameRoomGroupUser gameRoomGroupUser : userList) {
            userIdList.add(gameRoomGroupUser.getUserId());
        }
        Result<List<SysUserInfoMoneyVo>> userResult = userService.findListByUserIdList(userIdList);
        if (userResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return list;
        }
        List<SysUserInfoMoneyVo> userResultData = userResult.getDatas();
        GameRoomGroupUserVo userVo = null;
        for (SysUserInfoMoneyVo vo : userResultData) {
            userVo = new GameRoomGroupUserVo();
            //其他玩家隐藏部分账号信息，暂时交前端处理，前端要通过用户名匹配数据
//            String userName = vo.getUserName();
//            if (StringUtils.isNotBlank(userName) && !userName.equals(sysUser.getUsername())) {
//                if (userName.length() > 3) {
//                    userName = userName.substring(0, userName.length() - 3);
//                }
//                userName = userName + "***";
//            }
//            vo.setUserName(userName);
            BeanUtils.copyProperties(vo, userVo);
            list.add(userVo);
        }
        return list;
    }

    @Override
    public List<GameRoomGroupUserVo> getGroupList(String userName) {
        return gameRoomGroupUserMapper.getGroupList(userName);
    }

    @Override
    public void removeGroup(Long gameId, String tableNum, SysUser sysUser) {
        //先查询是否已存在
        GameRoomGroupUser user = gameRoomGroupUserMapper.checkExist(gameId, tableNum, sysUser.getId());
        if (user != null && user.getUserId() != null) {
            gameRoomGroupUserMapper.deleteById(user.getId());
            GameRoomGroupUserVo vo = new GameRoomGroupUserVo();
            vo.setGameId(gameId);
            vo.setTableNum(tableNum);
            vo.setStatus(0);
            vo.setUserName(user.getUserName());
            pushGameDataToClientService.syncTableNumGroup(vo);
        }
    }

    @Override
    public void removeAllGroup(String userName) {
        List<GameRoomGroupUserVo> groupList = gameRoomGroupUserMapper.getGroupList(userName);
        for (GameRoomGroupUserVo vo : groupList) {
            gameRoomGroupUserMapper.deleteById(vo.getId());
            vo.setStatus(0);
            pushGameDataToClientService.syncTableNumGroup(vo);
        }
    }
}
