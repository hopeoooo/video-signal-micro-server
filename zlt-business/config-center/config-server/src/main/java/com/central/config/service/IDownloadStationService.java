package com.central.config.service;

import com.central.common.model.Result;
import com.central.common.service.ISuperService;
import com.central.config.model.DownloadStation;

import java.util.List;


public interface IDownloadStationService extends ISuperService<DownloadStation> {

     List<DownloadStation> findDownloadStationList() ;

     Result saveOrUpdateDownloadStation(DownloadStation downloadStation) throws Exception;
}
