package com.central.user.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户洗码明细
 *
 * @author zlt
 * @date 2021-12-03 19:31:47
 */
@Data
@ApiModel("用户洗码明细")
public class WashCodeChangeVo{

    @ApiModelProperty(value = "游戏ID")
    private Long gameId;
    @ApiModelProperty(value = "游戏名称")
    private String gameName;
    @ApiModelProperty(value = "返水比例（%）")
    private String rate;
    @ApiModelProperty(value = "有效投注额")
    private BigDecimal validbet;
    @ApiModelProperty(value = "返水金额")
    private BigDecimal amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public BigDecimal getValidbet() {
        return keepDecimal(validbet);
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public BigDecimal getAmount() {
        return keepDecimal(amount);
    }

    private BigDecimal keepDecimal(BigDecimal val) {
        return val == null ? BigDecimal.ZERO.setScale(2) : val.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
