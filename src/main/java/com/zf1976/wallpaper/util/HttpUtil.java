package com.zf1976.wallpaper.util;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mac
 * @date 2021/3/21
 **/
public class HttpUtil {


    public static String getFileNameFromPath(String path) {
        // 从路径中获取文件名
        String fileName = StringUtil.subSuf(path, path.lastIndexOf('/') + 1);
        if (StrUtil.isBlank(fileName)) {
            // 编码后的路径做为文件名
            fileName = URLUtil.encodeQuery(path, CharsetUtil.CHARSET_UTF_8);
        }
        return fileName;
    }

    /**
     * 从Content-Disposition头中获取文件名
     *
     * @return 文件名，empty表示无
     */
    public static String getFileNameFromDisposition(String header) {
        String fileName = null;
        if (StrUtil.isNotBlank(header)) {
            fileName = pattern("filename=\"(.*?)\"", header, 1);
            if (StrUtil.isBlank(fileName)) {
                fileName = StrUtil.subAfter(header, "filename=", true);
            }
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
        final Pattern pattern = PatternPool.get(regex, Pattern.DOTALL);
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
