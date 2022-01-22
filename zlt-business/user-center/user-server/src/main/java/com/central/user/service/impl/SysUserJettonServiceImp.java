package com.central.user.service.impl;

import com.central.common.model.SysUserJetton;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.user.mapper.SysUserJettonMapper;
import com.central.user.service.ISysUserJettonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SysUserJettonServiceImp extends SuperServiceImpl<SysUserJettonMapper, SysUserJetton> implements ISysUserJettonService {
}
