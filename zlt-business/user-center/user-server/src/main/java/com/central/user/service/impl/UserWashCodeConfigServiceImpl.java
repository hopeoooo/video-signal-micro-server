package com.central.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.central.common.constant.CommonConstant;
import com.central.common.model.CodeEnum;
import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.constants.GameListEnum;
import com.central.game.feign.GameService;
import com.central.game.model.GameList;
import com.central.user.mapper.UserWashCodeConfigMapper;
import com.central.user.service.IUserWashCodeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@CacheConfig(cacheNames = {"userWashCodeConfig"})
public class UserWashCodeConfigServiceImpl extends SuperServiceImpl<UserWashCodeConfigMapper, UserWashCodeConfig> implements IUserWashCodeConfigService {

    @Autowired
    private GameService gameService;

    @Override
    public List<UserWashCodeConfig> findUserWashCodeConfigList(Long userId) {
        LambdaQueryWrapper<UserWashCodeConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWashCodeConfig::getUserId,userId);
        List<UserWashCodeConfig> userWashCodeConfigs = baseMapper.selectList(wrapper);
        return userWashCodeConfigs;
    }

    public Result saveCache(List<UserWashCodeConfig> list) {
        boolean b = saveOrUpdateBatch(list);
        return b ? Result.succeed(list, "操作成功") : Result.failed("操作失败");
    }

    @Override
    public List<UserWashCodeConfig> findWashCodeConfigList(Long userId) {
        List<UserWashCodeConfig> userWashCodeConfigList = findUserWashCodeConfigList(userId);
        if (!CollectionUtils.isEmpty(userWashCodeConfigList)) {
            return userWashCodeConfigList;
        }
        //个人配置为空查询全局配置
        Result<List<GameList>> listResult = gameService.findAllOpenRate();
        if (listResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return null;
        }
        List<GameList> datas = listResult.getDatas();
        if (CollectionUtils.isEmpty(datas)) {
            return null;
        }
        List<UserWashCodeConfig> washCodeConfigList = new ArrayList<>();
        for (GameList game : datas) {
            UserWashCodeConfig userWashCodeConfig = new UserWashCodeConfig();
            userWashCodeConfig.setUserId(userId);
            userWashCodeConfig.setGameId(game.getId());
            userWashCodeConfig.setGameName(game.getName());
            userWashCodeConfig.setGameRate(game.getGameRate());
            washCodeConfigList.add(userWashCodeConfig);
        }
        return washCodeConfigList;
    }

    @Override
    public List<UserWashCodeConfig> findWashCodeConfigListByUserId(Long userId) {
        List<UserWashCodeConfig> userWashCodeConfigList = new ArrayList<>();
        for (GameListEnum game : GameListEnum.values()) {
            List<UserWashCodeConfig> userWashCodeConfigs = findWashCodeConfigListByGameIdAndUserId(game.getGameId(),userId);
            userWashCodeConfigList.addAll(userWashCodeConfigs);
        }
        return userWashCodeConfigList;
    }

    @Override
    public List<UserWashCodeConfig> findWashCodeConfigListByGameIdAndUserId(Long gameId, Long userId) {
        LambdaQueryWrapper<UserWashCodeConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWashCodeConfig::getUserId, userId);
        wrapper.eq(UserWashCodeConfig::getGameId, gameId);
        List<UserWashCodeConfig> userWashCodeConfigs = baseMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(userWashCodeConfigs)) {
            Result<List<GameList>> gameListResult = gameService.findEnableGameListByGameId(gameId, CommonConstant.OPEN);
            if (gameListResult.getResp_code() != CodeEnum.SUCCESS.getCode()) {
                log.error("全局洗码配置查询失败，userId={},gameId={}", userId, gameId);
                return userWashCodeConfigs;
            }
            gameListResult.getDatas().forEach(info -> {
                UserWashCodeConfig cfg = new UserWashCodeConfig();
                cfg.setGameId(info.getId());
                cfg.setGameName(info.getName());
                cfg.setGameRate(info.getGameRate());
                userWashCodeConfigs.add(cfg);
            });
        }
        return userWashCodeConfigs;
    }

}
