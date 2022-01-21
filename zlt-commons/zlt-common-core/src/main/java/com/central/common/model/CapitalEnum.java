package com.central.common.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 账变记录相关枚举
 */
public enum CapitalEnum {

    TRANSFERIN(1, "转入"),
    TRANSFEROUT(2, "转出"),
    SETTLEMENTAMOUNT(3, "派彩"),
    BET(4, "下注"),
    ARTIFICIALIN(5, "手动入款"),
    ARTIFICIALOUT(6, "手动出款"),
    WASH_CODE(7, "领取洗码"),
    DEFAULT(-1, "未知");

    @Getter
    private final int type;

    @Getter
    private final String name;

    CapitalEnum(int type, String name){
        this.type = type;
        this.name = name;
    }

    public static CapitalEnum fingCapitalEnumType(Integer type){
        if(type == null){
            return DEFAULT;
        }
        for(CapitalEnum capitalEnum : CapitalEnum.values()){
            if(capitalEnum.type == type){
                return capitalEnum;
            }
        }
        return DEFAULT;
    }

    public static CapitalEnum findCapitalEnumByName(String name){
        if(StringUtils.isBlank(name)){
            return DEFAULT;
        }

        for(CapitalEnum capitalEnum : CapitalEnum.values()){
            if(StringUtils.equals(capitalEnum.name, name)){
                return capitalEnum;
            }
        }
        return DEFAULT;
    }

//    public static String toJson(){
//
//    }
}
