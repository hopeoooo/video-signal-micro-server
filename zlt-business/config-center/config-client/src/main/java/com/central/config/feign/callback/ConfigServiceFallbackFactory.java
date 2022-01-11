package com.central.config.feign.callback;

import com.central.common.feign.UserService;
import com.central.common.model.*;
import com.central.config.dto.TouristDto;
import com.central.config.feign.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.multipart.MultipartFile;

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
            public Result saveTourist(Map<String, String> params) {
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
            public Result findNoticeList(Map<String, Object> params) {
                log.error("findNoticeList查询公告异常:{}", params, cause);
                return Result.failed("查询失败");
            }

            @Override
            public Result deleteNoticeId(Long id) {
                log.error("deleteNoticeId删除公告异常:{}", id, cause);
                return Result.failed("更新失败");
            }

            @Override
            public Result updateEnabled(Map<String, Object> params) {
                log.error("updateEnabled更新公告状态异常:{}", params, cause);
                return Result.failed("更新失败");
            }

            @Override
            public Result saveOrUpdate(SysNotice sysNotice) {
                log.error("saveOrUpdate更新公告异常:{}", sysNotice, cause);
                return Result.failed("更新失败");
            }
            @Override
            public Result findBannerList() {
                log.error("findBannerList查询banner异常" ,cause);
                return Result.failed("查询失败");
            }

            @Override
            public Result delBannerId(Long id) {
                log.error("delBannerId删除banner异常:{}", id, cause);
                return Result.failed("删除失败");
            }

            @Override
            public Result updateState(Map<String, Object> params) {
                log.error("updateState更新banner状态异常:{}", params, cause);
                return Result.failed("更新失败");
            }

            @Override
            public Result saveOrUpdate(MultipartFile fileH5,
                                       MultipartFile fileWeb,
                                       Integer sort,
                                       String linkUrl,
                                       String startTime,
                                       String endTime,
                                       Integer startMode,
                                       Integer endMode,
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
            public Result upload(MultipartFile file) throws Exception {
                log.error("upload上传异常", cause);
                return Result.failed("编辑失败");
            }

            @Override
            public String avatarPictureInfo() {
                log.error("avatarPictureInfo查询默认头像异常" ,cause);
                return null;
            }
        };
    }
}
