package com.zf1976.wallpaper.datasource;

import com.zf1976.wallpaper.entity.NetbianEntity;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NetbianStoreStrategy implements FileStoreStrategy<NetbianEntity> {

    private final Logger log = Logger.getLogger("[FileStoreStrategy]");
    private final File file;

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
    public void store(NetbianEntity netbianEntity) throws IOException {
        FileOutputStream fos = new FileOutputStream(file,true);
        if(file.length()<1){
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos)) {
                objectOutputStream.writeObject(netbianEntity);
            }
        }else {
            try (var objectOutputStream = new ObjectOutputStream(fos) {
                @Override
                protected void writeStreamHeader() {
                }
            }
            ) {
                objectOutputStream.writeObject(netbianEntity);
            }
        }
        fos.close();
    }

    @Override
    public List<NetbianEntity> read() throws IOException, ClassNotFoundException {
        List<NetbianEntity> list = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (fis.available() > 0) {
                NetbianEntity netbianEntity = (NetbianEntity) ois.readObject();
                list.add(netbianEntity);
            }
            return list;
        }
    }

}
