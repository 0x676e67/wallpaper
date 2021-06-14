package com.zf1976.wallpaper.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mac
 * @date 2021/3/21
 **/
public class HttpUtil {

    private static final Pattern FILENAME_PATTERN = Pattern.compile("filename=\"(.*?)\"");


    /**
     * 从Content-Disposition头中获取文件名
     *
     * @return 文件名，empty表示无
     */
    public static String getFileNameFromDisposition(String header) {
        String fileName = null;
        if (!StringUtil.isEmpty(header)) {
            fileName = pattern("filename=\"(.*?)\"", header, 1);
        }
        return fileName;
    }


    /**
     * 获得匹配的字符串
     *
     * @param regex 匹配的正则
     * @param content 被匹配的内容
     * @param groupIndex 匹配正则的分组序号
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String pattern(String regex, CharSequence content, int groupIndex) {
        if (null == content || null == regex) {
            return null;
        }
        final Pattern pattern = Pattern.compile(regex);
        return pattern(pattern, content, groupIndex);
    }


    /**
     * 获得匹配的字符串，对应分组0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     *
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @param groupIndex 匹配正则的分组序号，0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String pattern(Pattern pattern, CharSequence content, int groupIndex) {
        if (null == content || null == pattern) {
            return null;
        }

        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return null;
    }


}
