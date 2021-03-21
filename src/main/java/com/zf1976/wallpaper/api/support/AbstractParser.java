package com.zf1976.wallpaper.api.support;

import com.zf1976.wallpaper.api.constant.JsoupConstants;
import com.zf1976.wallpaper.api.support.impl.WallHavenParser;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author ant
 * Create by Ant on 2021/3/20 8:24 AM
 */
public abstract class AbstractParser implements IParser {

    protected final Logger logger = Logger.getLogger(this.getClass());
    protected final Connection connection;
    private Document document;

    protected AbstractParser(WallHavenParser.Builder builder){
        this.connection = Jsoup.connect(builder.getUrl())
                               .method(builder.getMethod())
                               .timeout(builder.getTimeout())
                               .proxy(builder.getProxy());
        try {
             document = this.connection.execute().parse();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 链接
     *
     * @param url wallpaper website
     * @return response
     */
    @Override
    public Connection.Response connect(String url) throws IOException {
        return this.connection.url(url)
                              .execute();
    }

    /**
     * 链接获取响应文档
     *
     * @param url wallpaper website
     * @return document
     */
    @Override
    public Document connection(String url) throws IOException {
        return connection.url(url)
                         .execute()
                         .parse();
    }

    /**
     * 获取标签
     *
     * @return document
     * @throws IOException throws
     */
    @Override
    public Elements getLabel(String label) throws IOException {
        return this.parserPattern(label);
    }

    /**
     * 获取包含href属性元素集合
     *
     * @return elements
     * @throws IOException throws
     */
    @Override
    public Elements parserHref() throws IOException {
        return this.parserPattern(JsoupConstants.ATTR_HREF);
    }

    /**
     * 获取包含href属性的<a></a>标签元素集合
     *
     * @return elements
     * @throws IOException throws
     */
    @Override
    public Elements parserLabelHref(String label) throws IOException {
        return this.parserPattern(JsoupConstants.A_HREF);
    }

    /**
     * 解析包含属性含有关联属性的元素集合
     *
     * @param label 标签
     * @param attr  属性
     * @return elements
     * @throws IOException throws
     */
    @Override
    public Elements parserLabelAttr(String label, String attr) throws IOException {
        return this.parserPattern(formatJoin(label, attr));
    }

    /**
     * 解析匹配模式
     *
     * @param pattern 模式
     * @return elements
     * @throws IOException throws
     */
    @Override
    public Elements parserPattern(String pattern) throws IOException {
        return this.document.select(pattern);
    }

    /**
     * 获取当前链接对象
     * @return connection
     */
    public Connection getConnection() {
        return connection;
    }

    public Document document(){
        return this.document;
    }
}
