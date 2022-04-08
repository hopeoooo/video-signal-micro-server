package com.central.game.service.impl;

import com.central.common.model.PushResult;
import com.central.common.model.Result;
import com.central.game.model.vo.LivePotVo;
import com.central.game.service.IPushGameDataToClientService;
import com.central.push.constant.SocketTypeConstant;
import com.central.push.feign.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class PushGameDataToClientServiceImpl implements IPushGameDataToClientService {

    @Autowired
    private PushService pushService;

    /**
     * 推送新增的投注数据
     *
     * @param gameId
     * @param tableNum
     * @param bootNum
     * @param bureauNum
     * @param newAddBetList
     */
    @Override
    @Async
    public void syncLivePot(Long gameId, String tableNum, String bootNum, String bureauNum, List<LivePotVo> newAddBetList) {
        if (CollectionUtils.isEmpty(newAddBetList)) {
            return;
        }
        String groupId = gameId + "-" + tableNum + "-" + bootNum + "-" + bureauNum;
        PushResult<List<LivePotVo>> pushResult = PushResult.succeed(newAddBetList, SocketTypeConstant.LIVE_POT, "即时彩池数据送成功");
        Result<String> push = pushService.sendMessageByGroupId(groupId, com.alibaba.fastjson.JSONObject.toJSONString(pushResult));
        log.info("即时彩池数据推送结果:groupId={},result={}", groupId, push);
    }
}
