package com.central.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * 
 *
 * @author lance
 * @date 2022-01-24 21:19:58
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("i18n_info")
public class I18nInfo extends SuperEntity {
    private static final long serialVersionUID=1L;

        private Long pageId;
        private Long positionId;
        private Integer from;
        private String zhCn;
        private String enUs;
        private String khm;
        private String th;
        private String operator;
        private Date updateTime;
    }
