package com.central.user.service;

import com.central.common.model.SysUserJetton;
import com.central.common.service.ISuperService;

public interface ISysUserJettonService extends ISuperService<SysUserJetton> {

    SysUserJetton queryJettonByUid(Long userId);

    SysUserJetton updateJettonConfig(String jettonConfig, Long userId);
}
