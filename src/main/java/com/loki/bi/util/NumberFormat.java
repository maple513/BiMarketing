package com.loki.bi.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author : loki
 * @version V1.0
 * @Project: BiMarketing
 * @Package com.loki.bi.util
 * @Description: TODO
 * @date Date : 2023 年 10月 28 日 10:04
 */
public class NumberFormat {

    /**
     * 将 12.32w 转换为 123200
     *
     * @param word
     * @return
     */
    public static long parseNumber(String word) {
        if (StrUtil.isNotEmpty(word)) {
            if (StrUtil.indexOf(word.toLowerCase(), 'w') > -1) {
                return (long) (NumberUtil.parseFloat(word.toLowerCase().replace("w", "")) * 10000L);
            } else {
                return NumberUtil.parseLong(word);
            }
        }
        return 0l;
    }
}
