package com.central.config.service;

import com.central.common.model.PageResult2;
import com.central.common.model.Result;
import com.central.common.service.ISuperService;
import com.central.config.model.DownloadStation;

import java.util.List;
import java.util.Map;


public interface IDownloadStationService extends ISuperService<DownloadStation> {

     PageResult2<DownloadStation> findDownloadStationList(Map<String, Object> map) ;

     Result saveOrUpdateDownloadStation(DownloadStation downloadStation) throws Exception;

      List<String> generateVersionNumber(String terminalType);
}
