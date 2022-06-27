package com.central.translate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.constant.I18nKeys;
import com.central.common.model.PageResult;
import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.I18nInfo;
import com.central.common.utils.I18nUtil;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.common.utils.StringUtils;
import com.central.common.vo.LanguageLabelVO;
import com.central.translate.mapper.I18nInfoMapper;
import com.central.translate.model.co.I18nInfoPageMapperCo;
import com.central.translate.model.co.QueryI18nInfoPageCo;
import com.central.translate.model.co.SaveI18nInfoCo;
import com.central.translate.model.co.UpdateI18nInfoCo;
import com.central.translate.service.I18nInfosService;
import com.central.common.vo.I18nInfoPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 国际化字典服务实现类
 *
 * @author lance
 * @since 2022 -01-25 11:42:27
 */
@Slf4j
@Service
public class I18nInfosServiceImpl extends SuperServiceImpl<I18nInfoMapper, I18nInfo> implements I18nInfosService {

    @Autowired
    private I18nInfoMapper mapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<I18nInfo> findListByZhCn(Integer fromOf, String zhCn) {
        return mapper.findListByZhCn(fromOf,zhCn);
    }

    /**
     * 获取所有的后台国际化资源
     *
     * @return {@link I18nSourceDTO} 国际化资源
     * @author lance
     * @since 2022 -01-25 11:58:22
     */
    @Override
    public I18nSourceDTO getBackendFullI18nSource() {
        return I18nUtil.getBackendFullSource();
    }

    /**
     * 获取所有的前台国际化资源
     *
     * @return {@link I18nSourceDTO} 国际化资源
     * @author lance
     * @since 2022 -01-28 12:44:38
     */
    @Override
    public I18nSourceDTO getFrontFullI18nSource(Integer fromOf) {
        if (fromOf == I18nKeys.FRONT_APP){
            return I18nUtil.getFrontAppFullSource();
        }else if (fromOf == I18nKeys.FRONT_MESSAGE){
            return I18nUtil.getFrontMessageFullSource();
        }else {
            return I18nUtil.getFrontFullSource();
        }
    }

    /**
     * 初始化国际化资源redis缓存
     *
     * @author lance
     * @since 2022 -01-25 17:24:33
     */
    @Override
    @PostConstruct
    public void initI18nSourceRedis() {
        List<I18nInfo> infos = list();

        // 批量写入redis
        redisTemplate.executePipelined((RedisCallback<?>)c -> {
            for (I18nInfo f : infos) {
                if (I18nKeys.BACKEND.equals(f.getFromOf())) {
                    // 中文国际化
                    c.hSet(I18nKeys.Redis.Backend.ZH_CN_KEY.getBytes(StandardCharsets.UTF_8),
                        f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getZhCn().getBytes(StandardCharsets.UTF_8));

                    // 英文国际化
                    if (StrUtil.isNotBlank(f.getEnUs())) {
                        c.hSet(I18nKeys.Redis.Backend.EN_US_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getEnUs().getBytes(StandardCharsets.UTF_8));
                    }

                    // 高棉语国际化
                    if (StrUtil.isNotBlank(f.getKhm())) {
                        c.hSet(I18nKeys.Redis.Backend.KHM_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getKhm().getBytes(StandardCharsets.UTF_8));
                    }

                    // 泰文国际化
                    if (StrUtil.isNotBlank(f.getTh())) {
                        c.hSet(I18nKeys.Redis.Backend.TH_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getTh().getBytes(StandardCharsets.UTF_8));
                    }
                } else if (I18nKeys.FRONT_PC.equals(f.getFromOf())) {

                    // 中文国际化
                    c.hSet(I18nKeys.Redis.FrontPc.ZH_CN_KEY.getBytes(StandardCharsets.UTF_8),
                        f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getZhCn().getBytes(StandardCharsets.UTF_8));

                    // 英文国际化
                    if (StrUtil.isNotBlank(f.getEnUs())) {
                        c.hSet(I18nKeys.Redis.FrontPc.EN_US_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getEnUs().getBytes(StandardCharsets.UTF_8));
                    }

                    // 高棉语国际化
                    if (StrUtil.isNotBlank(f.getKhm())) {
                        c.hSet(I18nKeys.Redis.FrontPc.KHM_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getKhm().getBytes(StandardCharsets.UTF_8));
                    }

                    // 泰文国际化
                    if (StrUtil.isNotBlank(f.getTh())) {
                        c.hSet(I18nKeys.Redis.FrontPc.TH_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getTh().getBytes(StandardCharsets.UTF_8));
                    }
                } else if (I18nKeys.FRONT_APP.equals(f.getFromOf())) {

                    // 中文国际化
                    c.hSet(I18nKeys.Redis.FrontApp.ZH_CN_KEY.getBytes(StandardCharsets.UTF_8),
                        f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getZhCn().getBytes(StandardCharsets.UTF_8));

                    // 英文国际化
                    if (StrUtil.isNotBlank(f.getEnUs())) {
                        c.hSet(I18nKeys.Redis.FrontApp.EN_US_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getEnUs().getBytes(StandardCharsets.UTF_8));
                    }

                    // 高棉语国际化
                    if (StrUtil.isNotBlank(f.getKhm())) {
                        c.hSet(I18nKeys.Redis.FrontApp.KHM_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getKhm().getBytes(StandardCharsets.UTF_8));
                    }

                    // 泰文国际化
                    if (StrUtil.isNotBlank(f.getTh())) {
                        c.hSet(I18nKeys.Redis.FrontApp.TH_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getTh().getBytes(StandardCharsets.UTF_8));
                    }
                } else if (I18nKeys.FRONT_MESSAGE.equals(f.getFromOf())) {

                    // 中文国际化
                    c.hSet(I18nKeys.Redis.FrontMessage.ZH_CN_KEY.getBytes(StandardCharsets.UTF_8),
                        f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getZhCn().getBytes(StandardCharsets.UTF_8));

                    // 英文国际化
                    if (StrUtil.isNotBlank(f.getEnUs())) {
                        c.hSet(I18nKeys.Redis.FrontMessage.EN_US_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getEnUs().getBytes(StandardCharsets.UTF_8));
                    }

                    // 高棉语国际化
                    if (StrUtil.isNotBlank(f.getKhm())) {
                        c.hSet(I18nKeys.Redis.FrontMessage.KHM_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getKhm().getBytes(StandardCharsets.UTF_8));
                    }

                    // 泰文国际化
                    if (StrUtil.isNotBlank(f.getTh())) {
                        c.hSet(I18nKeys.Redis.FrontMessage.TH_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8), f.getTh().getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
            return null;
        });

    }

    /**
     * 更新国际化字典
     *
     * @param from 0前台 1后台
     * @param param 入参释义
     * @return {@link boolean} 出参释义
     * @author lance
     * @since 2022 -02-17 20:30:46
     */
    @Override
    public boolean updateI18nInfo(Integer from, UpdateI18nInfoCo param) {
        boolean zhcnChange = StrUtil.isNotBlank(param.getZhCn());
        boolean enusChange = StrUtil.isNotBlank(param.getEnUs());
        boolean khmChange = StrUtil.isNotBlank(param.getKhm());
        boolean thChange = StrUtil.isNotBlank(param.getTh());
        I18nInfo info = getById(param.getId());
        if (null == info) {
            return false;
        }
        List<I18nInfo> listByZhCn = findListByZhCn(from, param.getZhCn());
        if (CollUtil.isNotEmpty(listByZhCn)){
            for (I18nInfo i18nInfo:listByZhCn){
                if (i18nInfo.getId().longValue() != param.getId().longValue()){
                    return false;
                }
            }
        }
        LambdaUpdateWrapper<I18nInfo> update = Wrappers.lambdaUpdate(I18nInfo.class).eq(I18nInfo::getId, param.getId())
            .eq(I18nInfo::getFromOf, from).set(param.getPageId() != null, I18nInfo::getPageId, param.getPageId())
            .set(param.getPositionId() != null, I18nInfo::getPositionId, param.getPositionId())
            .set(zhcnChange, I18nInfo::getZhCn, param.getZhCn()).set(enusChange, I18nInfo::getEnUs, param.getEnUs())
            .set(khmChange, I18nInfo::getKhm, param.getKhm()).set(thChange, I18nInfo::getTh, param.getTh())
            .set(StrUtil.isNotBlank(param.getOperator()), I18nInfo::getOperator, param.getOperator())
            .set(I18nInfo::getUpdateTime, new Date());
        int count = mapper.update(null, update);
        boolean succeed = count > 0;

        if (succeed) {
            String oldKey = null;
            if (!info.getZhCn().equals(param.getZhCn())){
                oldKey = info.getZhCn();
            }
            // 更新redis
            updateI18nRedis(from, param, info,oldKey);
        }

        return succeed;
    }

    /**
     * 新增国际化字典
     *
     * @param from 0前台 1后台
     * @param param 入参释义
     * @return {@link boolean} 出参释义
     * @author lance
     * @since 2022 -02-17 20:36:55
     */
    @Override
    public boolean saveI18nInfo(Integer from, SaveI18nInfoCo param) {
        I18nInfo info = new I18nInfo();
        BeanUtil.copyProperties(param, info);
        info.setFromOf(from);
        List<I18nInfo> listByZhCn = findListByZhCn(from, param.getZhCn());
        if (CollUtil.isNotEmpty(listByZhCn) && listByZhCn.size() >= 1){
            return false;
        }
        if (Objects.isNull(info.getEnUs())){
            info.setEnUs("");
        }
        if (Objects.isNull(info.getKhm())){
            info.setKhm("");
        }
        if (Objects.isNull(info.getTh())){
            info.setTh("");
        }
        boolean succeed = save(info);
        if (succeed) {
            updateI18nRedis(from, param, info,null);
        }
        return succeed;
    }

    private void updateI18nRedis(Integer from, SaveI18nInfoCo param, I18nInfo info,String oldKey) {
        boolean zhcnChange = StrUtil.isNotBlank(param.getZhCn());
        boolean enusChange = StrUtil.isNotBlank(param.getEnUs());
        boolean khmChange = StrUtil.isNotBlank(param.getKhm());
        boolean thChange = StrUtil.isNotBlank(param.getTh());

        // 更新redis
        String i18nKey = info.getZhCn();
        String redisKey = "";
        if (zhcnChange) {
            // 更新中文key
            i18nKey = param.getZhCn();
            if (I18nKeys.FRONT_PC.equals(from)) {
                redisKey = I18nKeys.Redis.FrontPc.ZH_CN_KEY;
            } else if (I18nKeys.FRONT_APP.equals(from)) {
                redisKey = I18nKeys.Redis.FrontApp.ZH_CN_KEY;
            } else if (I18nKeys.FRONT_MESSAGE.equals(from)) {
                redisKey = I18nKeys.Redis.FrontMessage.ZH_CN_KEY;
            } else {
                redisKey = I18nKeys.Redis.Backend.ZH_CN_KEY;
            }
            // 更新中文国际化
            I18nUtil.resetSource(redisKey, i18nKey, param.getZhCn());
            if (StringUtils.isNotEmpty(oldKey)){
                I18nUtil.deleteByKey(redisKey,oldKey);
            }
        }
        if (enusChange) {
            // 更新英文国际化
            if (I18nKeys.FRONT_PC.equals(from)) {
                redisKey = I18nKeys.Redis.FrontPc.EN_US_KEY;
            } else if (I18nKeys.FRONT_APP.equals(from)) {
                redisKey = I18nKeys.Redis.FrontApp.EN_US_KEY;
            } else if (I18nKeys.FRONT_MESSAGE.equals(from)) {
                redisKey = I18nKeys.Redis.FrontMessage.EN_US_KEY;
            } else {
                redisKey = I18nKeys.Redis.Backend.EN_US_KEY;
            }
            I18nUtil.resetSource(redisKey, i18nKey, param.getEnUs());
            if (StringUtils.isNotEmpty(oldKey)){
                I18nUtil.deleteByKey(redisKey,oldKey);
            }
        }
        if (khmChange) {
            // 更新高棉语国际化
            if (I18nKeys.FRONT_PC.equals(from)) {
                redisKey = I18nKeys.Redis.FrontPc.KHM_KEY;
            } else if (I18nKeys.FRONT_APP.equals(from)) {
                redisKey = I18nKeys.Redis.FrontApp.KHM_KEY;
            } else if (I18nKeys.FRONT_MESSAGE.equals(from)) {
                redisKey = I18nKeys.Redis.FrontMessage.KHM_KEY;
            } else {
                redisKey = I18nKeys.Redis.Backend.KHM_KEY;
            }
            I18nUtil.resetSource(redisKey, i18nKey, param.getKhm());
            if (StringUtils.isNotEmpty(oldKey)){
                I18nUtil.deleteByKey(redisKey,oldKey);
            }
        }
        if (thChange) {
            // 更新泰语国际化
            if (I18nKeys.FRONT_PC.equals(from)) {
                redisKey = I18nKeys.Redis.FrontPc.TH_KEY;
            } else if (I18nKeys.FRONT_APP.equals(from)) {
                redisKey = I18nKeys.Redis.FrontApp.TH_KEY;
            } else if (I18nKeys.FRONT_MESSAGE.equals(from)) {
                redisKey = I18nKeys.Redis.FrontMessage.TH_KEY;
            } else {
                redisKey = I18nKeys.Redis.Backend.TH_KEY;
            }
            I18nUtil.resetSource(redisKey, i18nKey, param.getTh());
            if (StringUtils.isNotEmpty(oldKey)){
                I18nUtil.deleteByKey(redisKey,oldKey);
            }
        }
    }

    /**
     * 查询国际化字典分页
     *
     * @param param 查询参数
     * @return {@link PageResult} 分页数据
     * @author lance
     * @since 2022 -01-25 12:25:12
     */
    @Override
    public PageResult<I18nInfoPageVO> findInfos(QueryI18nInfoPageCo param) {
        Page<I18nInfoPageVO> page = new Page<>(param.getPage(), param.getLimit());
        I18nInfoPageMapperCo params = new I18nInfoPageMapperCo();

        if (StrUtil.isNotBlank(param.getWord()) && param.getLanguage() != null) {
            switch (param.getLanguage()) {
                case I18nKeys.LocaleCode.ZH_CN:
                    params.setZhCn(param.getWord());
                    break;
                case I18nKeys.LocaleCode.EN_US:
                    params.setEnUs(param.getWord());
                    break;
                case I18nKeys.LocaleCode.KHM:
                    params.setKhm(param.getWord());
                    break;
                case I18nKeys.LocaleCode.TH:
                    params.setTh(param.getWord());
                    break;
            }
        }

        params.setPageId(param.getPageId());
        params.setPositionId(param.getPositionId());
        params.setFrom(param.getFrom());

        List<I18nInfoPageVO> list = mapper.findPage(page, params);
        for (I18nInfoPageVO vo : list) {
            // 国际化处理
            vo.setPage(I18nUtil.t(vo.getPage()));
            vo.setPosition(I18nUtil.t(vo.getPosition()));
        }
        long total = page.getTotal();

        return PageResult.<I18nInfoPageVO>builder().data(list).count(total).build();
    }

    /**
     * 获取语言标签
     *
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-28 13:34:54
     */
    @Override
    public List<LanguageLabelVO> getLanguageLabel() {
        return Arrays.asList(new LanguageLabelVO(I18nKeys.LocaleCode.ZH_CN, I18nUtil.t("中文")),
            new LanguageLabelVO(I18nKeys.LocaleCode.EN_US, I18nUtil.t("英文")),
            new LanguageLabelVO(I18nKeys.LocaleCode.KHM, I18nUtil.t("柬埔寨语")),
            new LanguageLabelVO(I18nKeys.LocaleCode.TH, I18nUtil.t("泰语")));
    }
}
