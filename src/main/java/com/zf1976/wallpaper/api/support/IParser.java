package com.zf1976.wallpaper.api.support;

import com.zf1976.wallpaper.util.StringUtil;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author mac
 * @date 2021/3/18
 **/
public interface IParser {

    /**
     * 链接
     *
     * @date 2021-03-20 23:29:28
     * @param url url
     * @return org.jsoup.Connection.Response
     * @throws IOException e
     */
    Connection.Response connect(String url) throws IOException;

    /**
     * 链接获取响应文档
     *
     * @date 2021-03-20 23:30:59
     * @param url url
     * @return org.jsoup.nodes.Document
     * @throws IOException e
     */
    Document connection(String url) throws IOException;

    /**
     * 获取标签元素集合
     *
     * @date 2021-03-20 23:32:47
     * @param label 标签
     * @return org.jsoup.select.Elements
     * @throws IOException e
     */
    Elements getLabel(String label) throws IOException;

    /**
     * 解析包含href属性元素集合
     *
     * @date 2021-03-20 23:33:26
     * @return org.jsoup.select.Elements
     * @throws IOException e
     */
    Elements parserHref() throws IOException;

    /**
     * 解析包含href属性的<a></a>标签元素集合
     *
     * @date 2021-03-20 23:35:03
     * @param label 标签
     * @return org.jsoup.select.Elements
     * @throws IOException e
     */
    Elements parserLabelHref(String label) throws IOException;

    /**
     * 解析包含属性含有关联属性的元素集合
     *
     * @date 2021-03-20 23:36:10
     * @param label 标签
     * @param attr 属性
     * @return org.jsoup.select.Elements
     * @throws IOException e
     */
    Elements parserLabelAttr(String label, String attr) throws IOException;

    /**
     * 解析匹配模式
     *
     * @date 2021-03-20 23:35:52
     * @param pattern 模式
     * @return org.jsoup.select.Elements
     * @throws IOException e
     */
    Elements parserPattern(String pattern) throws IOException;

    /**
     * 格式化联结符
     *
     * @date 2021-03-20 23:35:25
     * @param label 标签
     * @param attr 属性
     * @return java.lang.String
     */
    default String formatJoin(String label, String attr) {
        return label + StringUtil.BRACKET_START + attr + StringUtil.BRACKET_END;
    }
}
