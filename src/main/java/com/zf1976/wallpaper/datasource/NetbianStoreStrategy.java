package com.zf1976.wallpaper.datasource;

import com.zf1976.wallpaper.entity.NetbianEntity;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NetbianStoreStrategy implements FileStoreStrategy<NetbianEntity> {

    private final Logger log = Logger.getLogger("[FileStoreStrategy]");
    private final File file;
    private final List<NetbianEntity> cacheNetbianEntityList = new LinkedList<>();

    public NetbianStoreStrategy() {
        this(Paths.get(System.getProperty("user.home"),
                "netbian.txt")
                  .toFile());
    }

    public NetbianStoreStrategy(File file) {
        this.file = file;
        try {
            this.checkFileExist();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkFileExist() throws IOException {
        if (!file.exists()) {
            if (file.createNewFile()) {
                log.info("create file:" + file.getAbsolutePath());
            }
        }
    }


    @Override
    public void store(NetbianEntity netbianEntity) {
        this.cacheNetbianEntityList.clear();
        if (netbianEntity.getDataId() == null) {
            throw new RuntimeException("dataId cannot been null!");
        }
        if (!this.container(netbianEntity)) {
            try (FileOutputStream fos = new FileOutputStream(file, true)) {
                if (file.length() > 0) {
                    var objectOutputStream = new ObjectOutputStream(fos) {
                        @Override
                        protected void writeStreamHeader() {
                        }
                    };
                    objectOutputStream.writeObject(netbianEntity);
                    objectOutputStream.close();
                } else {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
                    objectOutputStream.writeObject(netbianEntity);
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e.getCause());
            }
        }
    }

    @Override
    public List<NetbianEntity> read() {
        if (!this.cacheNetbianEntityList.isEmpty()) {
            return this.cacheNetbianEntityList;
        }
        List<NetbianEntity> list = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file)) {
            if (fis.available() <= 0) {
                return Collections.emptyList();
            }
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));
            while (fis.available() > 0) {
                NetbianEntity netbianEntity = (NetbianEntity) ois.readObject();
                list.add(netbianEntity);
            }
            ois.close();
            this.cacheNetbianEntityList.addAll(list);
            return this.cacheNetbianEntityList;
        } catch (ClassNotFoundException | IOException e) {
            log.error(e.getMessage(), e.getCause());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean container(NetbianEntity netbianEntity) {
        // distinct
        var netbianEntityList = this.read();
        if (!netbianEntityList.isEmpty()) {
            return netbianEntityList.stream()
                                    .anyMatch(var -> var.getDataId()
                                                        .equals(netbianEntity.getDataId()));
        }
        return false;
    }

    @Override
    public boolean container(String id) {
        var netbianEntity = new NetbianEntity().setDataId(id);
        return this.container(netbianEntity);
    }

}
