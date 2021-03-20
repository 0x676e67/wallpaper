package com.zf1976.wallpaper.api;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author mac
 * @date 2021/3/18
 **/
public interface IParser {

    String START = "[";
    String END = "]";

    /**
     * 链接
     *
     * @param url wallpaper website
     * @return response
     * @throws IOException throws
     */
    Connection.Response connect(String url) throws IOException;

    /**
     * 链接获取响应文档
     *
     * @param url wallpaper website
     * @return document
     * @throws IOException throws
     */
    Document connection(String url) throws IOException;

    /**
     * 获取标签元素集合
     *
     * @param label 标签名
     * @return elements
     * @throws IOException throws
     */
    Elements getLabel(String label) throws IOException;

    /**
     * 解析包含href属性元素集合
     *
     * @return elements
     * @throws IOException throws
     */
    Elements parserHref() throws IOException;

    /**
     * 解析包含href属性的<a></a>标签元素集合
     *
     * @param label 标签
     * @return elements
     * @throws IOException throws
     */
    Elements parserLabelHref(String label) throws IOException;

    /**
     * 解析包含属性含有关联属性的元素集合
     *
     * @param label 标签
     * @param attr 属性
     * @return elements
     * @throws IOException throws
     */
    Elements parserLabelAttr(String label, String attr) throws IOException;

    /**
     * 解析匹配模式
     *
     * @param pattern 模式
     * @return elements
     * @throws IOException throws
     */
    Elements parserPattern(String pattern) throws IOException;

    /**
     * 格式化联结符
     *
     * @param label 标签
     * @param attr 属性
     * @return format
     */
    default String formatJoin(String label, String attr) {
        return label + START + attr + END;
    }
}
