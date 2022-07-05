package com.central.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.constant.CommonConstant;
import com.central.common.constant.I18nKeys;
import com.central.common.model.CodeEnum;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.utils.DateUtil;
import com.central.common.utils.ServletUtil;
import com.central.config.feign.ConfigService;
import com.central.game.mapper.GameListMapper;
import com.central.game.model.GameList;
import com.central.game.model.GameRoomList;
import com.central.game.service.IGameListService;
import com.central.game.service.IGameRoomGroupUserService;
import com.central.game.service.IPushGameDataToClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.central.common.model.SuperPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.service.impl.SuperServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 *
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Slf4j
@Service
@CacheConfig(cacheNames = {"gameList"})
public class GameListServiceImpl extends SuperServiceImpl<GameListMapper, GameList> implements IGameListService {

    @Autowired
    private IGameRoomGroupUserService gameRoomGroupUserService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private IPushGameDataToClientService pushGameDataToClientService;

    @Override
    public void updateGameStatus(Map<String, Object> params) {
        baseMapper.updateGameStatus(params);
    }

    /**
     * 列表
     * @param superPage
     * @return
     */
    @Override
    public PageResult<GameList> findList(SuperPage superPage){
        Page<GameList> page = new Page<>(superPage.getPage(), superPage.getLimit());
        List<GameList> list  =  baseMapper.findList(page, superPage);
        return PageResult.<GameList>builder().data(list).count(page.getTotal()).build();
    }

    @Override
//    @Cacheable(key = "#p0")
    public GameList findById(Long gameId) {
        return baseMapper.selectById(gameId);
    }

    @Override
    public List<GameList> findAll() {
        List<GameList> gameLists = baseMapper.findEnableAllGame();
        //查询最低在线人数
        Result<String> result = configService.findMinOnlineUserQuantity();
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            log.error("查询最低在线人数失败,result={}", result.toString());
        }
        String datas = result.getDatas();
        for (GameList game : gameLists) {
            //多语言,其他语言统一取英文
            HttpServletRequest request = ServletUtil.getHttpServletRequest();
            String language = request.getHeader(I18nKeys.LANGUAGE);
            if(!I18nKeys.Locale.ZH_CN.equalsIgnoreCase(language)){
                game.setName(game.getEnName());
            }
            //游戏维护状态判断，不在维护时间区间的算正常
            if (!ObjectUtils.isEmpty(game.getGameStatus()) && game.getGameStatus() == CommonConstant.MAINTAIN) {
                boolean maintain = DateUtil.isEffectiveDate(new Date(), game.getMaintainStart(), game.getMaintainEnd());
                //当前时间不在维护时间区间内属于正常状态
                if (!maintain) {
                    game.setGameStatus(CommonConstant.NORMAL);
                    game.setMaintainStart(null);
                    game.setMaintainEnd(null);
                }
            }
            Integer onlineNum = gameRoomGroupUserService.getGameOnlineNum(game.getId());
            if (!ObjectUtils.isEmpty(datas)) {
                onlineNum = onlineNum + Integer.parseInt(datas);
            }
            game.setOnlineNum(onlineNum);
        }
        return gameLists;
    }

    @Override
    public void syncPushGameStatus(GameList gameList) {
        //判断桌台维护状态
        if (!ObjectUtils.isEmpty(gameList.getGameStatus()) && gameList.getGameStatus() == CommonConstant.MAINTAIN) {
            boolean maintain = DateUtil.isEffectiveDate(new Date(), gameList.getMaintainStart(), gameList.getMaintainEnd());
            //当前时间不在维护时间区间内属于正常状态
            if (!maintain) {
                gameList.setGameStatus(CommonConstant.NORMAL);
                gameList.setMaintainStart(null);
                gameList.setMaintainEnd(null);
            }
        }
        //异步通知客户端
        pushGameDataToClientService.syncPushGameStatus(gameList);
    }
}
