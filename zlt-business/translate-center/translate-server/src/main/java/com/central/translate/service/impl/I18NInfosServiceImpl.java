package com.central.translate.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.model.PageResult;
import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.I18nInfo;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.translate.mapper.I18nInfoMapper;
import com.central.common.params.translate.I18nInfoPageMapperParam;
import com.central.common.params.translate.QueryI18nInfoPageParam;
import com.central.common.params.translate.UpdateI18nInfoParam;
import com.central.translate.service.I18nInfosService;
import com.central.common.vo.I18nInfoPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
@CacheConfig(cacheNames = {"i18n"})
public class I18NInfosServiceImpl extends SuperServiceImpl<I18nInfoMapper, I18nInfo> implements I18nInfosService{

    @Autowired
    private I18nInfoMapper mapper;

    /**
     * 获取所有的国际化资源
     *
     * @return {@link I18nSourceDTO} 国际化资源
     * @author lance
     * @since 2022 -01-25 11:58:22
     */
    @Override
    @Cacheable(cacheNames = "fullSource")
    public I18nSourceDTO getFullI18nSource() {
        List<I18nInfo> i18nInfoList = list();
        I18nSourceDTO dto = new I18nSourceDTO();
        for (I18nInfo f: i18nInfoList) {
            dto.getZhCn().put(f.getZhCn(), f.getZhCn());
            dto.getEnUs().put(f.getZhCn(), f.getEnUs());
            dto.getKhm().put(f.getZhCn(), f.getKhm());
            dto.getTh().put(f.getZhCn(), f.getTh());
        }
        return dto;
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
        LambdaUpdateWrapper<I18nInfo> update = Wrappers.lambdaUpdate(I18nInfo.class)
                .eq(I18nInfo::getId, param.getId())
                .set(param.getPageId() != null, I18nInfo::getPageId, param.getPageId())
                .set(param.getPositionId() != null, I18nInfo::getPositionId, param.getPositionId())
                .set(StrUtil.isNotBlank(param.getZhCn()), I18nInfo::getZhCn, param.getZhCn())
                .set(StrUtil.isNotBlank(param.getEnUs()), I18nInfo::getEnUs, param.getEnUs())
                .set(StrUtil.isNotBlank(param.getKhm()), I18nInfo::getKhm, param.getKhm())
                .set(StrUtil.isNotBlank(param.getTh()), I18nInfo::getTh, param.getTh())
                .set(StrUtil.isNotBlank(operator), I18nInfo::getOperator, operator)
                .set(I18nInfo::getUpdateTime, new Date());
        int count = mapper.update(null, update);
        return count > 0;
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
                case 0:
                    params.setZhCn(param.getWord());
                    break;
                case 1:
                    params.setEnUs(param.getWord());
                    break;
                case 2:
                    params.setKhm(param.getWord());
                    break;
                case 3:
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
