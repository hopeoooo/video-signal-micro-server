package com.central.config.service;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.co.PageCo;
import com.central.common.service.ISuperService;
import com.central.config.model.DownloadStation;

import java.util.List;
import java.util.Map;


public interface IDownloadStationService extends ISuperService<DownloadStation> {

     PageResult<DownloadStation> findDownloadStationList(PageCo map) ;

     Result saveOrUpdateDownloadStation(DownloadStation downloadStation) throws Exception;

      List<String> generateVersionNumber(String terminalType);


    DownloadStation selectById(Long id) ;
}
