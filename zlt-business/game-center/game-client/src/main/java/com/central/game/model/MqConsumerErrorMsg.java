package com.central.game.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.central.common.model.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zlt
 * @date 2022-01-04 14:14:35
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("mq_consumer_error_msg")
@ApiModel("MQ消费者错误消息")
public class MqConsumerErrorMsg extends SuperEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "错误信息")
    private String exMessage;
    @ApiModelProperty(value = "路由")
    private String routingKey;
    @ApiModelProperty(value = "交换机")
    private String exchange;
    @ApiModelProperty("错误堆栈信息")
    private String exStackTrace;
    @ApiModelProperty(value = "消息内容")
    private String message;
}
