package com.central.common.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 账变记录相关枚举
 */
public enum CapitalEnum {

    TRANSFERIN(1, "转入",1),
    TRANSFEROUT(2, "转出",0),
    SETTLEMENTAMOUNT(3, "派彩",0),
    BET(4, "下注",1),
    ARTIFICIALIN(5, "手动入款",0),
    ARTIFICIALOUT(6, "手动出款",1),
    WASH_CODE(7, "领取洗码",0),
    BUSINESS_ADD(8, "商户API加点",0),
    BUSINESS_SUB(9, "商户API扣点",1),
    DEFAULT(-1, "未知",2);

    @Getter
    private final int type;

    @Getter
    private final String name;

    /**
     * 0.加钱，1.减钱
     */
    @Getter
    private final int addOrSub;

    CapitalEnum(int type, String name,int addOrSub){
        this.type = type;
        this.name = name;
        this.addOrSub = addOrSub;
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
