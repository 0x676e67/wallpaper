package com.zf1976.wallpaper.datasource;

import com.zf1976.wallpaper.entity.NetbianEntity;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetbianStoreStrategy implements FileStoreStrategy<NetbianEntity> {

    private final Logger log = Logger.getLogger("[FileStoreStrategy]");
    private final File file;

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
        if (netbianEntity.getDataId() == null) {
            throw new RuntimeException("dataId cannot been null!");
        }
        if (!this.container(netbianEntity)) {
            try {
                FileOutputStream fos = new FileOutputStream(file, true);
                if (file.length() <= 0) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
                    objectOutputStream.writeObject(netbianEntity);
                    objectOutputStream.close();
                } else {
                    var objectOutputStream = new ObjectOutputStream(fos) {
                        @Override
                        protected void writeStreamHeader() {
                        }
                    };
                    objectOutputStream.writeObject(netbianEntity);
                    objectOutputStream.close();
                }
                fos.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e.getCause());
            }
        }
    }

    @Override
    public List<NetbianEntity> read() {
        List<NetbianEntity> list = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (fis.available() > 0) {
                NetbianEntity netbianEntity = (NetbianEntity) ois.readObject();
                list.add(netbianEntity);
            }
            return list;
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
                                    .anyMatch(var -> netbianEntity.getDataId()
                                                                  .equals(var.getDataId()));
        }
        return false;
    }

}
