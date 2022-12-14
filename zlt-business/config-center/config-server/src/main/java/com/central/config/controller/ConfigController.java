package com.central.config.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.central.common.model.Result;
import com.central.config.constants.ConfigConstants;
import com.central.config.dto.BetMultipleDto;
import com.central.config.dto.TouristDto;
import com.central.config.dto.logoUrlDto;
import com.central.config.model.SysAvatarPicture;
import com.central.config.model.SysPlatformConfig;
import com.central.config.model.co.BetMultipleCo;
import com.central.config.model.co.SaveTouristCo;
import com.central.config.service.ISysAvatarPictureService;
import com.central.config.service.ISysPlatformConfigService;
import com.central.file.feign.FileService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置模块
 */
@Slf4j
@RestController
@Api(tags = "配置模块api")
@RequestMapping("/system")
public class ConfigController {
    @Autowired
    private ISysPlatformConfigService platformConfigService;

    @Autowired
    private ISysAvatarPictureService avatarPictureService;

    @Resource
    private FileService fileService;

    @Value("${zlt.app.version:1.0.0}")
    private String version;

    @Value("${zlt.app.liveUrl:null}")
    private String liveUrl;


    @ApiOperation(value = "查询配置列表")
    @GetMapping("/list")
    public String list(){
        return "test.game";
    }

    @ApiOperation(value = "查询版本号")
    @GetMapping("/getVersion")
    public Result<String> getVersion(){
        return Result.succeed(version,"查询成功");
    }

    @ApiOperation(value = "查询直播流地址")
    @GetMapping("/getLiveUrl")
    public Result<String> getLiveUrl(){
        return Result.succeed(liveUrl,"查询成功");
    }

    /**
     * 全局参数:游客管理查询
     */
    @ApiOperation(value = "全局参数:游客管理查询")
    @GetMapping("/findTouristAmount")
    public Result<TouristDto> findTouristAmount() {
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        TouristDto platformConfigDto=new TouristDto();
        platformConfigDto.setTouristAmount(touristAmount.getTouristAmount());
        platformConfigDto.setTouristSingleMaxBet(touristAmount.getTouristSingleMaxBet());
        return Result.succeed(platformConfigDto, "查询成功");
    }

    /**
     * 全局参数:游客管理编辑
     * @param params
     * @return
     */
    @ApiOperation(value = "全局参数:游客管理编辑")
    @PostMapping("/saveTourist")
    public Result saveTourist(@Valid @ModelAttribute SaveTouristCo params) {
        //校验数字
        /*String regex = "^[0-9]*$";
        if (!params.get("touristAmount").matches(regex) || !params.get("touristSingleMaxBet").matches(regex)) {
            return Result.failed("金额只能输入数字");
        }*/
        BigDecimal touristAmount = params.getTouristAmount();
        BigDecimal touristSingleMaxBet = params.getTouristSingleMaxBet();
        if(touristAmount.compareTo(BigDecimal.ZERO)==-1 || touristSingleMaxBet.compareTo(BigDecimal.ZERO)==-1){
            return Result.failed("金额不能小于0");
        }
        return platformConfigService.saveCache(touristAmount,touristSingleMaxBet);
    }

    /**
     * 金钱符号查询
     * @return
     */
    @ApiOperation("金钱符号查询")
    @GetMapping("/findMoneySymbol")
    public Result<String> findMoneySymbol(){
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        String moneySymbol = touristAmount.getMoneySymbol() == null ? "￥" : touristAmount.getMoneySymbol();
        return Result.succeed(moneySymbol, "查询成功");
    }


    /**
     * 修改金钱符号
     * @return
     */
    @ApiOperation("编辑金钱符号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moneySymbol", value = "金钱符号", required = true),
    })
    @PostMapping("/updateMoneySymbol")
    public Result updateMoneySymbol(@RequestParam("moneySymbol") String moneySymbol){
        if (StrUtil.isBlank(moneySymbol)){
            return Result.failed("参数错误");
        }
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        if (touristAmount==null){
            return Result.failed("更新失败");
        }
        touristAmount.setMoneySymbol(moneySymbol);
        boolean save = platformConfigService.saveOrUpdate(touristAmount);
        return save  ? Result.succeed("更新成功") : Result.failed("更新失败");
    }

    /**
     * logo查询
     * @return
     */
    @ApiOperation("logo查询")
    @GetMapping("/findLogoUrlInfo")
    public Result<logoUrlDto> findLogoUrlInfo(){
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        if (touristAmount==null){
            return Result.succeed( "查询失败");
        }
        logoUrlDto logoUrlDto=new logoUrlDto();
        logoUrlDto.setLogImageUrlApp(touristAmount.getLogImageUrlApp());
        logoUrlDto.setLogImageUrlPc(touristAmount.getLogImageUrlPc());
        logoUrlDto.setLoginRegisterLogImageUrlApp(touristAmount.getLoginRegisterLogImageUrlApp());
        logoUrlDto.setWebsiteIcon(touristAmount.getWebsiteIcon());
        return Result.succeed(logoUrlDto, "查询成功");
    }


    /**
     * 编辑logo图(Pc)
     * @param file
     * @return
     * @throws Exception
     */
    @ApiOperation("编辑logo图")
    @PostMapping(value = "/saveLogoPicturePc",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result saveLogoPicturePc(@RequestPart(value = "file", required = true) MultipartFile file,@RequestParam("type") Integer type) {
        //调用上传
        Map<String, String> upload = upload(file);
        String url= upload.get("url");
        String fileId= upload.get("fileId");
        Boolean saveLogo = false;
        if (url != null) {
            SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
            if (touristAmount == null) {
                Result.succeed("编辑失败");
            }
            if (type == ConfigConstants.icon) {
                touristAmount.setWebsiteIcon(url);
            }
            if (type == ConfigConstants.pc) {
                touristAmount.setLogImageUrlPc(url);
            }
            if (type == ConfigConstants.app) {
                touristAmount.setLogImageUrlApp(url);
            }
            if (type == ConfigConstants.appLoginRegistration) {
                touristAmount.setLoginRegisterLogImageUrlApp(url);
            }
            saveLogo = platformConfigService.saveOrUpdate(touristAmount);
        }
        return saveLogo ? Result.succeed("编辑成功") : Result.failed("编辑失败");
    }


    /**
     * 查询头像列表
     */
    @ApiOperation("查询头像列表")
    @ResponseBody
    @GetMapping("/findAvatarPictureList")
    public Result findAvatarPictureList() {
        List<SysAvatarPicture> avatarPictureList = avatarPictureService.findAvatarPictureList();
        return Result.succeed(avatarPictureList,"查询成功");
    }


    /**
     * 查询默认头像
     */
    @ApiOperation("查询默认头像地址")
    @ResponseBody
    @GetMapping("/avatarPictureInfo")
    public String avatarPictureInfo() {
        return avatarPictureService.avatarPictureInfo();
    }

    /**
     * 上传头像
     * @param file
     * @return
     */
    @ApiOperation("上传头像")
    @ResponseBody
    @RequestMapping(value = "/saveAvatarPicture",method = {RequestMethod.POST})
    public Result saveAvatarPicture(@RequestPart(value = "file", required = true) MultipartFile[] file) {
        Boolean save=false;
        List<SysAvatarPicture> list=new ArrayList<>();
        if(file!=null&&file.length>0){
            //循环获取file数组中得文件
            for(int i = 0;i<file.length;i++){
                MultipartFile files = file[i];
                //校验文件大小
                Boolean result = verifySize(files);
                if (!result){
                    return Result.failed("文件不能大于2M");
                }
                //校验格式
                Boolean aBoolean = verifyFormat(files.getOriginalFilename());
                if (!aBoolean){
                    return Result.failed("格式错误");
                }
                //上传图片
                Map<String, String> upload = upload(files);
                String url= upload.get("url");
                String fileId= upload.get("fileId");
                if (url!=null){
                    SysAvatarPicture avatarPicture=new SysAvatarPicture();
                    avatarPicture.setUrl(url);
                    avatarPicture.setFileId(fileId);
                    list.add(avatarPicture);
                }
            }
            if (list!=null && list.size()>0){
                save = avatarPictureService.saveAvatarPicture(list);
            }
        }
        return save  ? Result.succeed("上传成功") : Result.failed("上传失败");
    }

    /**
     * 删除
     *
     * @param id
     */
    @ApiOperation("删除头像")
    @DeleteMapping(value = "/delAvatarPictureId/{id}")
    public Result delAvatarPictureId(@PathVariable Long id) {
        //查询banner是否存在
        SysAvatarPicture sysAvatarPicture = avatarPictureService.selectById(id);
        if (sysAvatarPicture==null){
            return Result.failed("头像不存在");
        }
        boolean b = avatarPictureService.delAvatarPictureId(id);
        if (StrUtil.isNotEmpty(sysAvatarPicture.getFileId())){
            fileService.delete(sysAvatarPicture.getFileId());
        }
        return b ? Result.succeed("删除成功") : Result.failed("删除失败");
    }
    /**
     * 查询最低在线人数
     * @return
     */
    @ApiOperation("查询最低在线人数")
    @GetMapping("/findMinOnlineUserQuantity")
    public Result<String> findMinOnlineUserQuantity(){
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        if (touristAmount==null){
            return Result.failed("查询失败");
        }
        String minOnlineUserQuantity = touristAmount.getMinOnlineUserQuantity() == null ? "0" : touristAmount.getMinOnlineUserQuantity();
        return Result.succeed(minOnlineUserQuantity, "查询成功");
    }



    /**
     * 修改最低在线人数
     * @return
     */
    @ApiOperation("编辑最低在线人数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "minOnlineUserQuantity", value = "最低在线人数", required = true),
    })
    @PostMapping("/updateMinOnlineUserQuantity")
    public Result updateMinOnlineUserQuantity(@RequestParam("minOnlineUserQuantity") String minOnlineUserQuantity){
        //校验数字
        String regex = "^[0-9]*$";
        if (!minOnlineUserQuantity.matches(regex)) {
            return Result.failed("无效参数,请重新输入");
        }
        if (StrUtil.isBlank(minOnlineUserQuantity)){
            return Result.failed("参数错误");
        }
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        if (touristAmount==null){
            return Result.failed("更新失败");
        }
        touristAmount.setMinOnlineUserQuantity(minOnlineUserQuantity);
        boolean save = platformConfigService.saveOrUpdate(touristAmount);
        return save  ? Result.succeed("更新成功") : Result.failed("更新失败");
    }


    @ApiOperation(value = "打码量预设查询")
    @GetMapping("/findBetMultiple")
    public Result<BetMultipleDto> findBetMultiple() {
        SysPlatformConfig touristAmount = platformConfigService.findTouristAmount();
        BetMultipleDto betMultipleDto=new BetMultipleDto();
        betMultipleDto.setBetMultiple(touristAmount.getBetMultiple());
        betMultipleDto.setBetZrrorPint(touristAmount.getBetZrrorPint());
        return Result.succeed(betMultipleDto, "查询成功");
    }


    @ApiOperation(value = "修改打量预设查询")
    @PostMapping("/saveBetMultiple")
    public Result saveBetMultiple(@Valid @ModelAttribute BetMultipleCo params) {

        BigDecimal betMultiple = params.getBetMultiple();
        BigDecimal betZrrorPint = params.getBetZrrorPint();
        if(betMultiple.compareTo(BigDecimal.ZERO)==-1 || betZrrorPint.compareTo(BigDecimal.ZERO)==-1){
            return Result.failed("金额不能小于0");
        }
        return platformConfigService.saveBetMultiple(betMultiple,betZrrorPint);
    }


    /**
     * 校验格式
     * @param fileName
     * @return
     */
    public Boolean verifyFormat(String fileName){
        Boolean identification=false;
        //格式
        String substring = fileName.substring(fileName.lastIndexOf("."));
        if (substring.equals(ConfigConstants.bmp) ||substring.equals(ConfigConstants.gif)||substring.equals(ConfigConstants.jpeg)||substring.equals(ConfigConstants.jpg)){
            identification=true;
        }
        return identification;
    }

    /**
     * 校验文件大小
     * @param file
     * @return
     */
    public Boolean verifySize(MultipartFile file) {
        InputStream inputStream = null;
        try {
             inputStream = file.getInputStream();
            long size = inputStream.available();
            System.out.println("文件大小：" + size + " Byte");
            if (size == 0 || size > 2 * 1024 * 1024) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    /**
     * 上传
     * @param file
     * @return
     */
    public Map<String,String> upload(MultipartFile file){
        Map<String,String> info=new HashMap<>();
        Result upload = null;
        try {
            upload = fileService.upload(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (upload.getResp_code()==0){
            JSONObject JSONObject=new JSONObject(upload.getDatas());
            String fileId = String.valueOf(JSONObject.get("id"));
            String url=String.valueOf(JSONObject.get("url"));
            info.put("url",url);
            info.put("fileId",fileId);
        }
        return info;
    }
}
