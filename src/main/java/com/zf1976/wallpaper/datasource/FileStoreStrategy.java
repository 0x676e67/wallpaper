package com.zf1976.wallpaper.datasource;

import java.io.IOException;
import java.util.List;

public interface FileStoreStrategy<T> {

    void store(T t) throws IOException;

    List<T> read() throws IOException, ClassNotFoundException;

    boolean container(T t);
}
