package com.central.translate.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.constant.I18nKeys;
import com.central.common.model.PageResult;
import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.I18nInfo;
import com.central.common.redis.i18n.I18nUtil;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.translate.mapper.I18nInfoMapper;
import com.central.common.params.translate.I18nInfoPageMapperParam;
import com.central.common.params.translate.QueryI18nInfoPageParam;
import com.central.common.params.translate.UpdateI18nInfoParam;
import com.central.translate.service.I18nInfosService;
import com.central.common.vo.I18nInfoPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * 国际化字典服务实现类
 *
 * @author lance
 * @since 2022 -01-25 11:42:27
 */
@Slf4j
@Service
public class I18nInfosServiceImpl extends SuperServiceImpl<I18nInfoMapper, I18nInfo> implements I18nInfosService{

    @Autowired
    private I18nInfoMapper mapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取所有的国际化资源
     *
     * @return {@link I18nSourceDTO} 国际化资源
     * @author lance
     * @since 2022 -01-25 11:58:22
     */
    @Override
    public I18nSourceDTO getFullI18nSource() {
        return I18nUtil.getFullSource();
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
        List<I18nInfo> list = list();
        // 批量写入redis
        redisTemplate.executePipelined((RedisCallback<?>)  c -> {
            for (I18nInfo f : list) {
                // 英文国际化
                if (StrUtil.isNotBlank(f.getEnUs())) {
                    c.hSet(I18nKeys.Redis.EN_US_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8),
                            f.getEnUs().getBytes(StandardCharsets.UTF_8));
                }

                // 高棉语国际化
                if (StrUtil.isNotBlank(f.getKhm())) {
                    c.hSet(I18nKeys.Redis.KHM_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8),
                            f.getKhm().getBytes(StandardCharsets.UTF_8));
                }

                // 泰文国际化
                if (StrUtil.isNotBlank(f.getTh())) {
                    c.hSet(I18nKeys.Redis.TH_KEY.getBytes(StandardCharsets.UTF_8),
                            f.getZhCn().getBytes(StandardCharsets.UTF_8),
                            f.getTh().getBytes(StandardCharsets.UTF_8));
                }
            }
            return null;
        });

    }

    /**
     * 更新国际化字典
     *
     * @param operator 操作人
     * @param param    更新参数
     * @return {@link boolean} 是否成功
     * @author lance
     * @since 2022 -01-25 12:14:35
     */
    @Override
    public boolean updateI18nInfo(String operator, UpdateI18nInfoParam param) {

        boolean zhcnChange = StrUtil.isNotBlank(param.getZhCn());
        boolean enusChange = StrUtil.isNotBlank(param.getEnUs());
        boolean khmChange = StrUtil.isNotBlank(param.getKhm());
        boolean thChange = StrUtil.isNotBlank(param.getTh());
        I18nInfo info = getById(param.getId());
        if (null == info) {
            return false;
        }

        LambdaUpdateWrapper<I18nInfo> update = Wrappers.lambdaUpdate(I18nInfo.class)
                .eq(I18nInfo::getId, param.getId())
                .set(param.getPageId() != null, I18nInfo::getPageId, param.getPageId())
                .set(param.getPositionId() != null, I18nInfo::getPositionId, param.getPositionId())
                .set(zhcnChange, I18nInfo::getZhCn, param.getZhCn())
                .set(enusChange, I18nInfo::getEnUs, param.getEnUs())
                .set(khmChange, I18nInfo::getKhm, param.getKhm())
                .set(thChange, I18nInfo::getTh, param.getTh())
                .set(StrUtil.isNotBlank(operator), I18nInfo::getOperator, operator)
                .set(I18nInfo::getUpdateTime, new Date());
        int count = mapper.update(null, update);
        boolean succeed = count > 0;

        if (succeed) {
            // 更新redis
            String i18nKey = info.getZhCn();
            if (zhcnChange) {
                // 更新中文key
                i18nKey = param.getZhCn();
            }
            if (enusChange) {
                // 更新英文国际化
                I18nUtil.resetSource(I18nKeys.Locale.EN_US, i18nKey, param.getEnUs());
            }
            if (khmChange) {
                // 更新高棉语国际化
                I18nUtil.resetSource(I18nKeys.Locale.KHM, i18nKey, param.getKhm());
            }
            if (thChange) {
                // 更新泰语国际化
                I18nUtil.resetSource(I18nKeys.Locale.TH, i18nKey, param.getTh());
            }
        }

        return succeed;
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
    public PageResult<I18nInfoPageVO> findInfos(QueryI18nInfoPageParam param) {
        Page<I18nInfoPageVO> page = new Page<>(param.getPage(), param.getLimit());
        I18nInfoPageMapperParam params = new I18nInfoPageMapperParam();

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

        List<I18nInfoPageVO> list = mapper.findPage(page, params);
        long total = page.getTotal();

        return PageResult.<I18nInfoPageVO>builder().data(list).count(total).build();
    }
}
