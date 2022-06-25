package com.central.common.constant;

/**
 * 国际化Keys
 *
 * @author lance
 * @since 2022 -01-25 15:42:26
 */
public interface I18nKeys {

    /**
     * header 选择语言
     */
    String LANGUAGE = "language";

    /**
     * header 请求来源
     */
    String REQUEST_SOURCE = "requestSource";

    /**
     * header 前台请求
     */
    String FRONT = "front";

    /**
     * 前台PC
     */
    Integer FRONT_PC = 0;

    /**
     * 后台
     */
    Integer BACKEND = 1;

    /**
     * 前台PC
     */
    Integer FRONT_APP = 2;


    /**
     * 前台PC
     */
    Integer FRONT_MESSAGE = 3;
    /**
     * 语种
     */
    interface Locale {
        /**
         * 简体中文
         */
        String ZH_CN = "zh_cn";

        /**
         * 英文
         */
        String EN_US = "en_us";

        /**
         * 高棉语
         */
        String KHM = "khm";

        /**
         * 泰语
         */
        String TH = "th";
    }

    // 语种代码
    interface LocaleCode {
        int ZH_CN = 0;
        int EN_US = 1;
        int KHM = 2;
        int TH = 3;
    }

    interface Redis {
        // 前台PC
        interface FrontPc {
            /**
             * 中文国际化 key
             */
            String ZH_CN_KEY = "i18n:source:zh_cn:hash:" + I18nKeys.FRONT_PC;
            /**
             * 英文国际化 key
             */
            String EN_US_KEY = "i18n:source:en_us:hash:" + I18nKeys.FRONT_PC;

            /**
             * 高棉语国际化 key
             */
            String KHM_KEY = "i18n:source:khm:hash:" + I18nKeys.FRONT_PC;

            /**
             * 泰语国际化 key
             */
            String TH_KEY = "i18n:source:th:hash:" + I18nKeys.FRONT_PC;
        }

        // 前台移动端
        interface FrontApp {
            /**
             * 中文国际化 key
             */
            String ZH_CN_KEY = "i18n:source:zh_cn:hash:" + I18nKeys.FRONT_APP;
            /**
             * 英文国际化 key
             */
            String EN_US_KEY = "i18n:source:en_us:hash:" + I18nKeys.FRONT_APP;

            /**
             * 高棉语国际化 key
             */
            String KHM_KEY = "i18n:source:khm:hash:" + I18nKeys.FRONT_APP;

            /**
             * 泰语国际化 key
             */
            String TH_KEY = "i18n:source:th:hash:" + I18nKeys.FRONT_APP;
        }

        // 前台message
        interface FrontMessage{
            /**
             * 中文国际化 key
             */
            String ZH_CN_KEY = "i18n:source:zh_cn:hash:" + I18nKeys.FRONT_MESSAGE;
            /**
             * 英文国际化 key
             */
            String EN_US_KEY = "i18n:source:en_us:hash:" + I18nKeys.FRONT_MESSAGE;

            /**
             * 高棉语国际化 key
             */
            String KHM_KEY = "i18n:source:khm:hash:" + I18nKeys.FRONT_MESSAGE;

            /**
             * 泰语国际化 key
             */
            String TH_KEY = "i18n:source:th:hash:" + I18nKeys.FRONT_MESSAGE;
        }

        // 后台
        interface Backend {
            /**
             * 中文国际化 key
             */
            String ZH_CN_KEY = "i18n:source:zh_cn:hash:" + I18nKeys.BACKEND;
            /**
             * 英文国际化 key
             */
            String EN_US_KEY = "i18n:source:en_us:hash:" + I18nKeys.BACKEND;

            /**
             * 高棉语国际化 key
             */
            String KHM_KEY = "i18n:source:khm:hash:" + I18nKeys.BACKEND;

            /**
             * 泰语国际化 key
             */
            String TH_KEY = "i18n:source:th:hash:" + I18nKeys.BACKEND;
        }

    }

}
