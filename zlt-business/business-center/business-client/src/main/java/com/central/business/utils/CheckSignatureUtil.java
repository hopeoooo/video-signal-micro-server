package com.central.business.utils;

import com.central.business.constant.BusinessConstants;
import com.central.business.model.co.RegisterCo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

/**
 * 校验签名工具类
 */
public class CheckSignatureUtil {

    public static boolean checkSignature(String signatureKey, String signature) {
        if (StringUtils.isBlank(signatureKey) || StringUtils.isBlank(signature)) {
            return false;
        }
        String md5Val = DigestUtils.md5DigestAsHex(signatureKey.getBytes());
        if (!md5Val.equals(signature)) {
            return false;
        }
        return true;
    }

    public static boolean checkSignature(Object entity) {
        String signatureKey = getSignatureKey(entity);
        try {
            Field field = entity.getClass().getDeclaredField(BusinessConstants.SIGNATURE);
            field.setAccessible(true);
            Object signature = field.get(entity);
            String md5Val = DigestUtils.md5DigestAsHex(signatureKey.getBytes());
            if (!md5Val.equals(signature)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 入参按字段正序排列拼接成字符串
     *
     * @param entity
     * @return
     */
    public static String getSignatureKey(Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            try {
                Object val = field.get(entity);
                if (!BusinessConstants.SIGNATURE.equals(name)) {
                    treeMap.put(name, val);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        String signatureKey = "";
        for (Map.Entry entry : treeMap.entrySet()) {
            String name = entry.getKey().toString();
            if (ObjectUtils.isEmpty(entry.getValue())) {
                signatureKey = signatureKey + name + "=" + "&";
            } else {
                String value = entry.getValue().toString();
                signatureKey = signatureKey + name + "=" + value + "&";
            }
        }
        if (StringUtils.isNotBlank(signatureKey)) {
            signatureKey = signatureKey.substring(0, signatureKey.length() - 1);
        }
        return signatureKey;
    }

    public static void main(String[] args) {
        RegisterCo co = new RegisterCo();
        co.setUserName("123");
        co.setSignature("a29e3e286657d84dc730d439b2588e1c");
        System.out.println(checkSignature(co));
    }
}
