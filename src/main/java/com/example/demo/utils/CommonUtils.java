package com.example.demo.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CommonUtils {

    public static Double roundingWithBigDecimal(Double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
    }
}
