package com.central.config.install;

import com.central.config.model.DownloadStation;
import com.central.config.service.IDownloadStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
@Order(1)
public class Initialization implements CommandLineRunner {
    @Autowired
    private IDownloadStationService downloadStationService;

    @Override
    public void run(String... args) throws Exception {
        log.info("=================初始化数据开始=================");
        runDownloadStation();
        log.info("=================初始化数据结束=================");
    }

    private void runDownloadStation(){
        List<DownloadStation> downloadStationList = downloadStationService.findDownloadStationList();
        if (downloadStationList==null || downloadStationList.size()==0){
            List<DownloadStation> customer = downloadStationList();
            downloadStationService.saveBatch(customer);
        }
    }

    public List<DownloadStation> downloadStationList(){
        List<DownloadStation> customer = new ArrayList<>();
        DownloadStation ios=new DownloadStation();
        ios.setDownloadUrl("http://116.212.138.46:9010/GameCenter.ipa");
        ios.setIsForced(1);
        ios.setName("ios");
        ios.setRemark("ios");
        ios.setTerminalType(2);
        ios.setVersionNumber("1.0.1");
        customer.add(ios);


        DownloadStation android=new DownloadStation();
        android.setDownloadUrl("http://116.212.138.46:9010/GameCenter.ipa");
        android.setIsForced(1);
        android.setName("Android1.1.1版本");
        android.setRemark("Android1.1.1版本");
        android.setTerminalType(1);
        android.setVersionNumber("1.1.1");
        customer.add(android);
        return customer;
    }

}
