package com.zf1976.wallpaper.util;

import cn.hutool.core.util.StrUtil;

/**
 * @author mac
 * @date 2021/3/21
 **/
public class StringUtil {

    public static final String SPACE = " ";
    public static final String TAB = "	";
    public static final String DOT = ".";
    public static final String DOUBLE_DOT = "..";
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";
    public static final String EMPTY = "";
    public static final String NULL = "null";
    public static final String CR = "\r";
    public static final String LF = "\n";
    public static final String UNDERLINE = "_";
    public static final String DASHED = "-";
    public static final String COMMA = ",";
    public static final String DELIMIT_START = "{";
    public static final String DELIMIT_END = "}";
    public static final String BRACKET_START = "[";
    public static final String BRACKET_END = "]";
    public static final String COLON = ":";

    // ------------------------------------------------------------------------ Empty
    /**
     * 字符串是否为空，空的定义如下:<br>
     * 1、为null <br>
     * 2、为""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 切割指定位置之后部分的字符串
     *
     * @param string 字符串
     * @param fromIndex 切割开始的位置（包括）
     * @return 切割后后剩余的后半部分字符串
     */
    public static String subSuf(CharSequence string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return sub(string, fromIndex, string.length());
    }


    /**
     * 改进JDK subString<br>
     * index从0开始计算，最后一个字符为-1<br>
     * 如果from和to位置一样，返回 "" <br>
     * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
     * 如果经过修正的index中from大于to，则互换from和to example: <br>
     * abcdefgh 2 3 =》 c <br>
     * abcdefgh 2 -3 =》 cde <br>
     *
     * @param str String
     * @param fromIndex 开始的index（包括）
     * @param toIndex 结束的index（不包括）
     * @return 字串
     */
    public static String sub(CharSequence str, int fromIndex, int toIndex) {
        if (isEmpty(str)) {
            return str(str);
        }
        int len = str.length();

        if (fromIndex < 0) {
            fromIndex = len + fromIndex;
            if (fromIndex < 0) {
                fromIndex = 0;
            }
        } else if (fromIndex > len) {
            fromIndex = len;
        }

        if (toIndex < 0) {
            toIndex = len + toIndex;
            if (toIndex < 0) {
                toIndex = len;
            }
        } else if (toIndex > len) {
            toIndex = len;
        }

        if (toIndex < fromIndex) {
            int tmp = fromIndex;
            fromIndex = toIndex;
            toIndex = tmp;
        }

        if (fromIndex == toIndex) {
            return EMPTY;
        }

        return str.toString().substring(fromIndex, toIndex);
    }


    /**
     * {@link CharSequence} 转为字符串，null安全
     *
     * @param cs {@link CharSequence}
     * @return 字符串
     */
    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }


    /**
     * 格式化字符串<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     *
     * @param strPattern 字符串模板
     * @param argArray 参数列表
     * @return 结果
     */
    public static String format(final String strPattern, final Object... argArray) {
        if (isEmpty(strPattern) || ObjectUtils.isEmpty(argArray)) {
            return strPattern;
        }
        final int strPatternLength = strPattern.length();

        // 初始化定义好的长度以获得更好的性能
        StringBuilder stuff = new StringBuilder(strPatternLength + 50);

        // 记录已经处理到的位置
        int handledPosition = 0;
        // 占位符所在位置
        int delimitIndex;
        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            delimitIndex = strPattern.indexOf(StrUtil.EMPTY_JSON, handledPosition);
            if (delimitIndex == -1) {
                // 剩余部分无占位符
                if (handledPosition == 0) {
                    // 不带占位符的模板直接返回
                    return strPattern;
                }
                // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                stuff.append(strPattern, handledPosition, strPatternLength);
                return stuff.toString();
            }

            // 转义符
            if (delimitIndex > 0 && strPattern.charAt(delimitIndex - 1) == StrUtil.C_BACKSLASH) {
                // 转义符
                if (delimitIndex > 1 && strPattern.charAt(delimitIndex - 2) == StrUtil.C_BACKSLASH) {
                    // 双转义符
                    // 转义符之前还有一个转义符，占位符依旧有效
                    stuff.append(strPattern, handledPosition, delimitIndex - 1);
                    stuff.append(StrUtil.utf8Str(argArray[argIndex]));
                    handledPosition = delimitIndex + 2;
                } else {
                    // 占位符被转义
                    argIndex--;
                    stuff.append(strPattern, handledPosition, delimitIndex - 1);
                    stuff.append(StrUtil.C_DELIM_START);
                    handledPosition = delimitIndex + 1;
                }
            } else {
                // 正常占位符
                stuff.append(strPattern, handledPosition, delimitIndex);
                stuff.append(StrUtil.utf8Str(argArray[argIndex]));
                handledPosition = delimitIndex + 2;
            }
        }

        // append the characters following the last {} pair.
        // 加入最后一个占位符后所有的字符
        stuff.append(strPattern, handledPosition, strPattern.length());

        return stuff.toString();
    }
}
