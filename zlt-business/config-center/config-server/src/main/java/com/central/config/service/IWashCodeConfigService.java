package com.central.config.service;

import com.central.common.service.ISuperService;
import com.central.config.model.WashCodeConfig;

import java.util.List;


public interface IWashCodeConfigService extends ISuperService<WashCodeConfig> {

    List<WashCodeConfig> findWashCodeConfigList() ;
}
