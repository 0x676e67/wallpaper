package com.zf1976.wallpaper.support;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;

/**
 * @author mac
 * @date 2021/3/16
 **/
public class PrintProgressBar {
    //总大小
    private long size;
    //必须设置总大小
    public PrintProgressBar(long size) {
        this.size = size;
    }
    //配置
    //是否打印进度条
    private boolean printProgressBar = true;
    //是否打印速度
    private boolean printSpeed = true;
    //是否打印百分比
    private boolean printPercentage = true;
    //是否打印总大小
    private boolean printSize = true;
    //是否开启单位换算
    private boolean byteConversion = true;
    //进度条长度
    private int percentageLength = 100;
    //是否在结束时自动打印信息
    private boolean autoPrintTime = true;
    //减少打印次数
    private boolean print100 = true;
    //单位
    private String conversion = "个";

    public PrintProgressBar setSize(long size) {
        this.size = size;
        return this;
    }

    public PrintProgressBar setPrintProgressBar(boolean printProgressBar) {
        this.printProgressBar = printProgressBar;
        return this;
    }
    public PrintProgressBar setPrintSpeed(boolean printSpeed) {
        this.printSpeed = printSpeed;
        return this;
    }
    public PrintProgressBar setPrintPercentage(boolean printPercentage) {
        this.printPercentage = printPercentage;
        return this;
    }
    public PrintProgressBar setPrintSize(boolean printSize) {
        this.printSize = printSize;
        return this;
    }
    public PrintProgressBar setByteConversion(boolean byteConversion) {
        this.byteConversion = byteConversion;
        return this;
    }
    public PrintProgressBar setPercentageLength(int percentageLength) {
        this.percentageLength = percentageLength;
        return this;
    }
    public PrintProgressBar setAutoPrintTime(boolean autoPrintTime) {
        this.autoPrintTime = autoPrintTime;
        return this;
    }
    public PrintProgressBar setPrint100(boolean print100) {
        this.print100 = print100;
        return this;
    }
    public PrintProgressBar setConversion(String conversion) {
        this.conversion = conversion;
        return this;
    }

    /**
     * 最开始的时间
     */
    private long timeStart;
    /**
     * 完全结束的时间
     */
    private long timeEnd;
    /**
     * 已完成进度(百分比)
     */
    private double progress;
    /**
     * 已完成进度(数量)
     */
    private long count;
    /**
     * 速度
     */
    private long speedStart;
    /**
     * 记录单位时间内执行的数据
     */
    private long speedNum;
    /**
     * 记录速度值, 放在这里是因为不一定每一次打印都要刷新速度, 中间的间隔可以用记录在这里的旧数据
     */
    private long speed;
    /**
     * 记录完成百分比, 用于减少打印次数
     */
    private int flag;

    /**
     * 内部计算总完成量
     * @param count count
     */
    public void printAppend(long count) {
        this.count += count;
        print(this.count);
    }
    /**
     * 核心方法
     * @param count 当前完成的数量
     */
    public void print(long count) {
        //开始计时
        if (timeStart == 0) {
            timeStart = speedStart = System.currentTimeMillis();
        }
        //如果需要在下载完成后自动打印总耗时和平均速度, 需要每次都进行计算完成度, 当这个值不小于100则代表完成
        if (autoPrintTime) {
            progress = count * 100 / (size + 0.0);
        }
        double percentage = 0;
        //当前完成百分比
        //获取当前完成百分比
        {
            if (percentageLength != 100) {
                //自定义进度条长度后要根据进度条长度进行计算
                percentage = count * percentageLength / (size + 0.0);
            } else if (!autoPrintTime) {
                //默认的进度条长度并且没有开启结束自动打印, 需要在这里计算完成百分比
                percentage = count * 100 / (size + 0.0);
            } else {
                //默认的进度条长度并且 开启 了结束自动打印, 计算步骤以在上面完成, 无需再次计算
                percentage = progress;
            }
        }
        if (print100 && percentage < flag) {
            //当前进度还满足打印条件
            return;
        } else {
            flag++;
        }
        //准备打印
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\r");
        //不换行进行覆盖
        //打印进度条
        if (printProgressBar){
            stringBuilder.append("Progress [");
            for (int i = 0; i < percentage; i++) {
                stringBuilder.append("=");
            }
            stringBuilder.append(">");
            for (int i = 0; i < percentageLength - percentage; i++) {
                stringBuilder.append(" ");
            }
            stringBuilder.append("]");
        }
        //打印百分比
        if (printPercentage){
            if (percentageLength == 100) {
                //进度条长度默认100, 不用重新计算
                stringBuilder.append(String.format("%.2f", percentage));
            } else if(autoPrintTime) {
                //开启了结束后自动打印, 百分比已经计算, 不用重新计算
                stringBuilder.append(String.format("%.2f", progress));
            } else {
                //自定义了进度条长度并且关闭了结束后自动打印, 打印百分比要计算值
                stringBuilder.append(String.format("%.2f", count * 100 / (size + 0.0)));
            }
            stringBuilder.append("%");
        }
        //打印总大小
        if (printSize) {
            stringBuilder.append(" 总大小: ");
            if (byteConversion) {
                getByteConversion(stringBuilder, size, false);
            } else {
                stringBuilder.append(size);
                stringBuilder.append(conversion);
            }
        }
        //打印速度
        if (printSpeed){
            //获取当前时间
            long speedEnd = System.currentTimeMillis();
            //计算当前时间 减去 上次打印速度的时间
            long time = speedEnd - speedStart;
            //距离上次打印时间超过1秒才会更新速度数据
            if (time >= 1000 ||
                    (time != 0 && speedEnd - timeStart < 1000)) {
                //或者程序总执行时间还不到1秒也可以计算
                //当前进度减去上次记录的进度, 从毫秒转换到秒
                speed = (count - speedNum) * 1000 / time;
                //记录这次的进度, 给下次计算速度的时候提供数据
                speedNum = count;
                //记录这次的时间, 给下次计算速度的时候提供数据
                speedStart = speedEnd;
            }
            stringBuilder.append(" 速度: ");
            //开启单位换算
            {
                //进制转换
                if (byteConversion) {
                    getByteConversion(stringBuilder, speed, true);
                } else {
                    //不需要进制转换
                    stringBuilder.append(speed);
                    stringBuilder.append(conversion);
                    stringBuilder.append("/s");
                }
            }
        }
        System.out.print(stringBuilder);
        if (autoPrintTime) {
            //完成进度大于等于100则打印总耗时和平均速度
            if (progress >= 100) {
                printTime();
            }
        }
    }
    /**
     * 打印总耗时和平均每秒速度
     */
    public void printTime() {
        //设置结束时间
        if (timeEnd == 0) {
            timeEnd = System.currentTimeMillis();
        }
        //获取总时间
        long time = timeEnd - timeStart;
        //时间转换倍率
        int conversion = 1;
        //打印时间单位
        String timeConversion = "";
        //获取时间单位和转换倍率
        {
            if (time / 1000 >= 60 && time / 1000 < 60 * 60) {
                //大于等于一分钟, 小于一小时
                conversion = 60;
                timeConversion = "分钟";
            } else if (time / 1000 >= 60 * 60) {
                //大于等于一小时
                conversion = 60 * 60;
                timeConversion = "小时";
            } else {
                timeConversion = "秒";
            }
        }
        //准备打印
        StringBuilder stringBuilder = new StringBuilder();
        //打印时间
        {
            //刚刚打印完进度条, 需要换行
            stringBuilder.append("\n");
            stringBuilder.append("总共耗时: ");
            //总毫秒 转换成秒在 除 转换倍率 ---> 保留两位小数点
            stringBuilder.append(String.format("%.2f", (time + 0.0) / conversion / 1000));
            stringBuilder.append(timeConversion);
        }
        //打印平均速度
        {
            //time小于1必然发生 除0 异常
            if (time > 1) {
                stringBuilder.append("\n");
                stringBuilder.append("平均速度: ");
                //总大小 除 总时间(秒)
                double byteConversionCount = size / ((time + 0.0) / 1000);
                if (byteConversion) {
                    //进制转换
                    getByteConversion(stringBuilder, byteConversionCount, true);
                } else {
                    //不进制转换
                    stringBuilder.append(String.format("%.2f", byteConversionCount));
                    stringBuilder.append(this.conversion);
                    stringBuilder.append("/s");
                }
            }
        }
        //打印
        System.out.println(stringBuilder);
    }
    /**
     * 进制转换
     * @param stringBuilder 将转换后的数据放在这个StringBuilder中
     * @param num 需要转换的数据
     * @param printConversion 是否打印 “/s”
     */
    public void getByteConversion(StringBuilder stringBuilder, double num, boolean printConversion) {
        if (num < 1024) {
            stringBuilder.append(String.format("%.2f", num));
            stringBuilder.append("B");
        } else if (num < 1024 * 1024) {
            stringBuilder.append(String.format("%.2f", num / 1024));
            stringBuilder.append("KB");
        } else {
            stringBuilder.append(String.format("%.2f", num / 1024 / 1024));
            stringBuilder.append("MB");
        }
        if (printConversion) {
            stringBuilder.append("/s");
        }
    }
    public PrintProgressBar noPrintProgressBar() {
        printProgressBar = false;
        return this;
    }
    public PrintProgressBar noPrintSpeed() {
        printSpeed = false;
        return this;
    }
    public PrintProgressBar noPrintPercentage() {
        printPercentage = false;
        return this;
    }
    public PrintProgressBar noPrintSize() {
        printSize = false;
        return this;
    }
    public PrintProgressBar noByteConversion() {
        byteConversion = false;
        return this;
    }
    public PrintProgressBar noAutoPrintTime() {
        autoPrintTime = false;
        return this;
    }
    public PrintProgressBar noPrint100() {
        print100 = false;
        return this;
    }

    public static void main(String[] args) throws IOException {
        URL url = new URL("https://repo1.maven.org/maven2/org/springframework/spring-core/5.2.5.RELEASE/spring-core-5.2.5.RELEASE-javadoc.jar");
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        //获取文件大小
        long contentLengthLong = urlConnection.getContentLengthLong();
        String path = urlConnection.getURL().getPath();
        int i = path.lastIndexOf("/");
        String fileName = path.substring(i+1);
        //创建对象并且赋值总大小
        PrintProgressBar printProgressBar = new PrintProgressBar(contentLengthLong);
        //自定义配置
//                .setPrintProgressBar(false)//取消打印进度条
//                .setPrintSpeed(false)//取消打印速度
//                .setPrintPercentage(false)//取消打印百分比
//                .setPrintSize(false)//取消打印总大小
//                .setByteConversion(false)//需要字节转换
//                .setPercentageLength(75)//设置进度条长度
//                .setAutoPrintTime(false)//取消完成后自动打印总耗时和平均每秒速度
//                .setPrint100(false)//增加打印次数, 实时监控, 对性能有略微影响(在我的渣渣机子上打印20亿次仅影响10秒)
//                .setConversion("字节")//自定义单位(此配置需要关闭字节转换才有效果)
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(Paths.get(System.getProperty("user.home"),fileName).toFile()));
        byte[] data = new byte [urlConnection.getContentLength()];
        int len =0;
        while ((len = inputStream.read(data)) != -1) {
            //使用追加打印
            printProgressBar.printAppend(len);
            outputStream.write(data,0, len);
        }
//        int count = 0;
//        while ((len = inputStream.read(bytes)) != -1) {
//            count += len;
//            // 不 使用追加打印
//            printProgressBar.print(count);
//        }
    }

}
