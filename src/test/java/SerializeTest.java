import com.zf1976.wallpaper.entity.NetbianEntity;
import org.junit.Test;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SerializeTest {

    public static void set(File file, NetbianEntity netbianEntity) throws Exception{
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

    public static List<NetbianEntity> get(File file) throws Exception{
        List<NetbianEntity> list = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while(fis.available()>0){
                NetbianEntity netbianEntity = (NetbianEntity) ois.readObject();
                list.add(netbianEntity);
            }
            return list;
        }
    }
}
