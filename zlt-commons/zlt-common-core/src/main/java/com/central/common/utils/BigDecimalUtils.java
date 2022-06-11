package com.central.common.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {

    public static BigDecimal keepDecimal(BigDecimal val) {
        return val == null ? BigDecimal.ZERO.setScale(2) : val.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
