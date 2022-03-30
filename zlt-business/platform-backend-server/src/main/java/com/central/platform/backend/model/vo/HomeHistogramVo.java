package com.central.platform.backend.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HomeHistogramVo implements Serializable {

    private static final long serialVersionUID = -6875784399817195379L;

    @ApiModelProperty(value = "1:总盈亏 2:有效投注 3:存款 4:提款")
    private Integer type;
    //今日
    @ApiModelProperty(value = "今日")
    private BigDecimal today = BigDecimal.ZERO;
    //昨日
    @ApiModelProperty(value = "昨日")
    private BigDecimal yesterday = BigDecimal.ZERO;
    //本周
    @ApiModelProperty(value = "本周")
    private BigDecimal thisWeek = BigDecimal.ZERO;
    //上周
    @ApiModelProperty(value = "上周")
    private BigDecimal lastWeek = BigDecimal.ZERO;
    //本月
    @ApiModelProperty(value = "本月")
    private BigDecimal currentMonth = BigDecimal.ZERO;
    //上月
    @ApiModelProperty(value = "上月")
    private BigDecimal lastMonth = BigDecimal.ZERO;
    //近两月
    @ApiModelProperty(value = "近两月")
    private BigDecimal nearlyTwoMonths = BigDecimal.ZERO;
}
