package com.tmac.appsearch;

import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.util.regex.Pattern;

/**
 * Created by T_MAC on 2016/5/22.
 */

class PinyinUtils {


    private static final String CHINESE_REGEX = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
    static final Pattern sChinesePattern = Pattern.compile(CHINESE_REGEX);

    private static final HanyuPinyinOutputFormat sPinyinFormat = new HanyuPinyinOutputFormat();

    static {
        sPinyinFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        sPinyinFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    static HanyuPinyinOutputFormat getPinyinFormat() {
        return sPinyinFormat;
    }


}
