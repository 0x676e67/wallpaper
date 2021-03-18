package com.zf1976.wallpaper.api;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author mac
 * @date 2021/3/18
 **/
public class DocumentParser {

    private final Logger logger = Logger.getLogger(this.getClass());
    private final Proxy proxy;
    private DocumentParser(Proxy proxy) {
        this.proxy = proxy;
        this.logger.setLevel(Level.DEBUG);
    }

    public Elements getMetaDate(String url, String keyword) {
        try {
            return Jsoup.connect(url)
                        .proxy(this.proxy)
                        .get()
                        .select(keyword);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static ProxyBuilder builder(){
        return new ProxyBuilder();
    }

    public static class ProxyBuilder {
        private String host;
        private int port = -1;
        private Proxy.Type type;

        public ProxyBuilder() {}

        public ProxyBuilder host(String host){
            this.host = host;
            return this;
        }

        public ProxyBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ProxyBuilder type(Proxy.Type type) {
            this.type = type;
            return this;
        }

        public DocumentParser noProxy() {
            return new DocumentParser(Proxy.NO_PROXY);
        }

        public DocumentParser build() {
            Proxy proxy;
            if (host == null || port == -1 || type == null) {
                proxy = Proxy.NO_PROXY;
            } else {
                proxy = new Proxy(this.type, InetSocketAddress.createUnresolved(this.host, this.port));
            }
            return new DocumentParser(proxy);
        }
    }
}
