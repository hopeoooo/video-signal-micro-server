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
        GameRoomGroupUserVo vo = new GameRoomGroupUserVo();
        vo.setGameId(gameId);
        vo.setTableNum(tableNum);
        vo.setGroupId(gameRoomGroupUser.getGroupId());
        vo.setStatus(2);
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(sysUser.getId());
        Result<List<SysUserInfoMoneyVo>> result = userService.findListByUserIdList(userIdList);
        if (result.getResp_code() == CodeEnum.SUCCESS.getCode()) {
            if (!CollectionUtils.isEmpty(result.getDatas())) {
                SysUserInfoMoneyVo userInfoMoneyVo = result.getDatas().get(0);
                vo.setMoney(userInfoMoneyVo.getMoney());
                vo.setHeadImgUrl(userInfoMoneyVo.getHeadImgUrl());
                vo.setUserName(sysUser.getUsername());
            }
        }
        pushGameDataToClientService.syncTableNumGroup(vo);
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
        GameRoomGroupUserVo userVo = null;
        for (GameRoomGroupUser gameRoomGroupUser : userList) {
            userVo = new GameRoomGroupUserVo();
            userVo.setGroupId(gameRoomGroupUser.getGroupId());
            userVo.setUserName(gameRoomGroupUser.getUserName());
            userVo.setGameId(gameId);
            userVo.setTableNum(tableNum);
            list.add(userVo);
            userIdList.add(gameRoomGroupUser.getUserId());
        }
        Result<List<SysUserInfoMoneyVo>> userResult = userService.findListByUserIdList(userIdList);
        if (userResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return list;
        }
        List<SysUserInfoMoneyVo> userResultData = userResult.getDatas();
        for(GameRoomGroupUserVo groupUserVo:list){
            for (SysUserInfoMoneyVo vo : userResultData) {
                if (groupUserVo.getUserName().equals(vo.getUserName())){
                    groupUserVo.setHeadImgUrl(vo.getHeadImgUrl());
                    groupUserVo.setMoney(vo.getMoney());
                    break;
                }
            }
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
            //查询当前分组是否还有人没有成员删除分组
            deleteGroup(user.getGroupId());
            GameRoomGroupUserVo vo = new GameRoomGroupUserVo();
            vo.setGroupId(user.getGroupId());
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
            //查询当前分组是否还有人没有成员删除分组
            deleteGroup(vo.getGroupId());
            vo.setStatus(0);
            pushGameDataToClientService.syncTableNumGroup(vo);
        }
    }

    //分组没有成员时删除分组
    public void deleteGroup(Long groupId) {
        LambdaQueryWrapper<GameRoomGroupUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameRoomGroupUser::getGroupId, groupId);
        Integer count = gameRoomGroupUserMapper.selectCount(lqw);
        if (count == 0) {
            gameRoomGroupService.removeById(groupId);
        }
    }
}
