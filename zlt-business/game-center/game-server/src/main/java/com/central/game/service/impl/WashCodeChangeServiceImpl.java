package com.central.game.service.impl;

import com.central.common.constant.I18nKeys;
import com.central.common.model.CodeEnum;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.UserWashCodeConfig;
import com.central.common.utils.ServletUtil;
import com.central.game.mapper.WashCodeChangeMapper;
import com.central.game.model.WashCodeChange;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.game.service.IWashCodeChangeService;
import com.central.user.feign.UserService;
import com.central.user.model.vo.WashCodeChangeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WashCodeChangeServiceImpl extends SuperServiceImpl<WashCodeChangeMapper, WashCodeChange> implements IWashCodeChangeService {

    @Autowired
    private WashCodeChangeMapper mapper;
    @Autowired
    private UserService userService;

    @Override
    public List<WashCodeChangeVo> getWashCodeRecord(Long userId, String startTime, String endTime) {
        List<WashCodeChangeVo> resultList = new ArrayList<>();
        //查询最新的洗码返水配置
        Result<List<UserWashCodeConfig>> codeConfigResult = userService.findWashCodeConfigListByUserId(userId);
        if (codeConfigResult.getResp_code()!= CodeEnum.SUCCESS.getCode()){
            return resultList;
        }
        List<UserWashCodeConfig> washCodeConfigList = codeConfigResult.getDatas();
        if (CollectionUtils.isEmpty(washCodeConfigList)) {
            return resultList;
        }
        List<WashCodeChange> recordList = mapper.getWashCodeRecord(userId, startTime, endTime);
        for (UserWashCodeConfig washCodeConfig : washCodeConfigList) {
            WashCodeChangeVo vo = new WashCodeChangeVo();
            vo.setGameId(washCodeConfig.getGameId());
            //多语言,其他语言统一取英文
            HttpServletRequest request = ServletUtil.getHttpServletRequest();
            String language = request.getHeader(I18nKeys.LANGUAGE);
            vo.setGameName(washCodeConfig.getGameName());
            if(!I18nKeys.Locale.ZH_CN.equalsIgnoreCase(language)){
                vo.setGameName(washCodeConfig.getGameEnName());
            }
            BigDecimal rate = washCodeConfig.getGameRate();
            BigDecimal gameRate = rate == null ? BigDecimal.ZERO.setScale(2) : rate.setScale(2, BigDecimal.ROUND_HALF_UP);
            vo.setRate(gameRate + "%");
            resultList.add(vo);
            for (WashCodeChange washCodeChange : recordList) {
                if (washCodeConfig.getGameId() == washCodeChange.getGameId()) {
                    vo.setAmount(washCodeChange.getAmount());
                    vo.setValidbet(washCodeChange.getValidbet());
                    break;
                }
            }
        }
        return resultList;
    }
}
