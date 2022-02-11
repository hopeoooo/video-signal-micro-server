package com.central.translate.model.co;

import lombok.Data;

/**
 * 国际化字典mapper查询参数
 *
 * @author lance
 * @since 2022 -01-25 12:46:36
 */
@Data
public class I18nInfoPageMapperCo {

    private String zhCn;

    private String enUs;

    private String khm;

    private String th;

    private Long pageId;

    private Long positionId;

    private Integer from;

}
