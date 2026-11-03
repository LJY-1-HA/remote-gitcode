package com.example.foodorder.util;

import java.util.Locale;

/**
 * 金额格式化：统一输出两位小数并带人民币符号
 */
public final class MoneyUtil {

    private MoneyUtil() {
    }

    public static String yuan(double value) {
        return "¥" + String.format(Locale.CHINA, "%.2f", value);
    }
}
