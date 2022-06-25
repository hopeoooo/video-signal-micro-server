package com.central.config.feign.callback;

import com.central.common.model.*;
import com.central.common.model.co.PageCo;
import com.central.config.dto.BetMultipleDto;
import com.central.config.dto.TouristDto;
import com.central.config.feign.ConfigService;
import com.central.config.model.DownloadStation;
import com.central.config.model.SysBanner;
import com.central.config.model.SysNotice;
import com.central.config.model.co.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 降级工场
 */
@Slf4j
public class ConfigServiceFallbackFactory implements FallbackFactory<ConfigService> {

    @Override
    public ConfigService create(Throwable cause) {
        return new ConfigService() {
            @Override
            public String list() {
                return null;
            }

            @Override
            public Result<TouristDto> findTouristAmount() {
                log.error("查询游客管理异常", cause);
                return Result.failed(new TouristDto(),"查询游客管理失败");
            }

            @Override
            public Result saveTourist(SaveTouristCo params) {
                log.error("saveTourist编辑异常:{}", params, cause);
                return Result.failed("更新失败");
            }

            @Override
            public Result findMoneySymbol() {
                log.error("findMoneySymbol查询金额符号异常", cause);
                return Result.failed("查询失败");
            }

            @Override
            public Result updateMoneySymbol(String moneySymbol) {
                log.error("updateMoneySymbol编辑金额符号异常:{}", moneySymbol, cause);
                return Result.failed("更新失败");
            }

            @Override
            public Result findLogoUrlInfo() {
                log.error("findLogoUrlInfo查询金额符号异常", cause);
                return Result.failed("查询失败");
            }

            @Override
            public Result<List<SysNotice>> findNoticeList(FindNoticeCo params) {
                log.error("findNoticeList查询公告异常:{}", params, cause);
                return Result.failed("查询失败");
            }

            @Override
            public Result deleteNoticeId(Long id) {
                log.error("deleteNoticeId删除公告异常:{}", id, cause);
                return Result.failed("更新失败");
            }

            @Override
            public Result updateEnabled(UpdateNoticeCo params) {
                log.error("updateEnabled更新公告状态异常:{}", params, cause);
                return Result.failed("更新失败");
            }

            @Override
            public Result saveOrUpdate(SysNoticeCo sysNotice) {
                log.error("saveOrUpdate更新公告异常:{}", sysNotice, cause);
                return Result.failed("更新失败");
            }
            @Override
            public Result<List<SysBanner>> findBannerList() {
                log.error("findBannerList查询banner异常" ,cause);
                return Result.failed("查询失败");
            }

            @Override
            public Result delBannerId(Long id) {
                log.error("delBannerId删除banner异常:{}", id, cause);
                return Result.failed("删除失败");
            }

            @Override
            public Result updateState(BannerUpdateStateCo params) {
                log.error("updateState更新banner状态异常:{}", params, cause);
                return Result.failed("更新失败");
            }

            @Override
            public Result saveOrUpdate(MultipartFile fileH5,
                                       MultipartFile fileH5Horizontal,
                                       MultipartFile fileWeb,
                                       Integer sort,
                                       String linkUrl,
                                       Long id) {
                log.error("saveOrUpdate更新banner异常", cause);
                return Result.failed("更新失败");
            }

            @Override
            public Result findAvatarPictureList() {
                log.error("findAvatarPictureList查询头像异常" ,cause);
                return Result.failed("查询失败");
            }
            @Override
            public Result saveLogoPicturePc(MultipartFile file, Integer type) throws Exception {
                log.error("upload上传异常", cause);
                return Result.failed("编辑失败");
            }

            @Override
            public Result saveAvatarPicture(MultipartFile[] file) {
                log.error("saveAvatarPicture上传异常", cause);
                return Result.failed("编辑失败");
            }

            @Override
            public Result delAvatarPictureId(Long id) {
                log.error("delAvatarPictureId删除头像异常:{}", id, cause);
                return Result.failed("删除失败");
            }

            @Override
            public String avatarPictureInfo() {
                log.error("avatarPictureInfo查询默认头像异常" ,cause);
                return null;
            }
            @Override
            public PageResult<DownloadStation> findDownloadStationList(PageCo params) {
                log.error("findDownloadStationList查询异常" ,cause);
                return new PageResult<>();
            }

            @Override
            public Result saveOrUpdateDownloadStation(DownloadStationCo downloadStation) throws Exception {
                log.error("saveOrUpdateDownloadStation编辑异常:{}", downloadStation, cause);
                return Result.failed("编辑失败");
            }

            @Override
            public Result<List<String>> generateVersionNumber(String terminalType) {
                log.error("generateVersionNumber自动生成版本号异常:{}", terminalType, cause);
                return Result.failed("查询失败");
            }

            @Override
            public Result deleteDownloadStationId(Long id) {
                log.error("deleteDownloadStationId删除异常:{}", id, cause);
                return Result.failed("删除失败");
            }

            @Override
            public Result<String> findMinOnlineUserQuantity() {
                log.error("findMinOnlineUserQuantity查询异常" ,cause);
                return Result.failed("查询失败");
            }

            @Override
            public Result updateMinOnlineUserQuantity(String minOnlineUserQuantity) {
                log.error("updateMinOnlineUserQuantity编辑最低在线人数异常:{}", minOnlineUserQuantity, cause);
                return Result.failed("编辑失败");
            }

            @Override
            public Result<BetMultipleDto> findBetMultiple() {
                log.error("findBetMultiple查询异常" ,cause);
                return Result.failed("查询失败");
            }

            @Override
            public Result saveBetMultiple(BetMultipleCo params) {
                log.error("saveBetMultiple编辑异常:{}", params, cause);
                return Result.failed("更新失败");
            }
        };
    }
}
