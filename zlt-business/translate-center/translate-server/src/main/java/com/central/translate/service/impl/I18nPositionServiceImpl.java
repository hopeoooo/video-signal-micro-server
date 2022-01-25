package com.central.translate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.central.common.vo.I18nPositionVO;
import com.central.common.model.I18nPosition;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.translate.mapper.I18nPositionMapper;
import com.central.translate.service.I18nPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 国际化位置服务实现类
 * @author lance
 * @since 2022 -01-25 11:43:38
 */
@Slf4j
@Service
public class I18nPositionServiceImpl extends SuperServiceImpl<I18nPositionMapper, I18nPosition> implements I18nPositionService {

    @Autowired
    private I18nPositionMapper mapper;

    /**
     * 查询所有的页面
     *
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-25 13:04:41
     */
    @Override
    public List<I18nPositionVO> findAllPage() {
        LambdaQueryWrapper<I18nPosition> wrapper = Wrappers.lambdaQuery(I18nPosition.class)
                .eq(I18nPosition::getType, 0);
        List<I18nPosition> list = mapper.selectList(wrapper);
        List<I18nPositionVO> voList = new ArrayList<>();
        for (I18nPosition p : list) {
            I18nPositionVO vo = new I18nPositionVO();
            BeanUtil.copyProperties(p, vo);
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 查询子级
     *
     * @param pid 入参释义
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-25 13:05:23
     */
    @Override
    public List<I18nPositionVO> findByPid(Long pid) {
        LambdaQueryWrapper<I18nPosition> wrapper = Wrappers.lambdaQuery(I18nPosition.class)
                .eq(I18nPosition::getPid, pid);
        List<I18nPosition> list = getBaseMapper().selectList(wrapper);
        List<I18nPositionVO> voList = new ArrayList<>();
        for (I18nPosition p : list) {
            I18nPositionVO vo = new I18nPositionVO();
            BeanUtil.copyProperties(p, vo);
            voList.add(vo);
        }
        return voList;
    }
}
