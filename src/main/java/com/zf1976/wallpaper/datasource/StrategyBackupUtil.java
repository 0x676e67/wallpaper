package com.zf1976.wallpaper.datasource;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 零依赖sql备份恢复工具
 * @author ant
 * Create by Ant on 2021/3/16 8:58 AM
 */
public class StrategyBackupUtil {

    private static final Logger LOGGER = Logger.getLogger("[StrategyBackupUtil]");
    private static final String INDEX_END = ";";
    private static final String BLANK = "";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String MYSQL_DUMP = "mysqldump --defaults-extra-file=/etc/my.cnf wallpaper";
    private static final String MYSQL_RECOVER = "mysql --defaults-extra-file=/etc/my.cnf wallpaper < ";
    private final Pattern pattern = Pattern.compile("(/)([a-zA-Z]*?)(\\?)");
    private final Pattern patternDefault = Pattern.compile("([/])([a-zA-Z]*)");

    /**
     * 提取URl数据库名
     *
     * @date 2021-05-14 21:03:12
     * @param dataSource 数据源
     * @return {@link String}
     */
    private String extractDatabase() {
        try (Connection connection = DbConnectionUtil.createConnection()) {
            String url = connection.getMetaData().getURL();
            String database;
            final Matcher matcher = this.pattern.matcher(url);
            // 第一次匹配URL
            while (matcher.find()) {
                database = matcher.group(2);
                if (database != null)  {
                    return database;
                }
            }
            final Matcher matcherDefault = this.patternDefault.matcher(url);
            byte startIndex = 0;
            byte endIndex = 3;
            // 第二次匹配URL
            while (matcherDefault.find()) {
                ++startIndex;
                if (startIndex == endIndex) {
                    database = matcherDefault.group(2);
                    if (database != null) {
                        return database;
                    }
                }
            }
            throw new RuntimeException("Cannot match data source name");
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("Invalid data source", e.getCause());
            throw new RuntimeException("Invalid datasource", e.getCause());
        }
    }

    public static boolean generatedBackupFile(String fileDirectory) {
        return generatedBackupFile(Paths.get(fileDirectory).toFile());
    }

    /**
     * 备份恢复
     *
     * @date 2021-03-21 21:59:28
     * @param absolutePath 绝对路径
     * @return boolean
     */
    public static boolean recover(String absolutePath) {
        return recover(Paths.get(absolutePath).toFile());
    }

    /**
     * 备份恢复
     *
     * @date 2021-03-21 21:59:28
     * @param sqlFilePath sql文件
     * @return boolean
     */
    public static boolean recover(File sqlFilePath){
        if(sqlFilePath.exists() && sqlFilePath.canRead() && sqlFilePath.length() > 0){
            String absolutePath = sqlFilePath.getAbsolutePath();
            try {
                Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", MYSQL_RECOVER + absolutePath});
                if(p.waitFor() == 0){
                    LOGGER.info("数据库恢复成功，数据来源 < " + absolutePath);
                    return true;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 生成备份文件
     *
     * @return boolean
     */
    public static boolean generatedBackupFile(File fileDirectory) {
        if (fileDirectory.isDirectory() || fileDirectory.exists()) {
            try {
                // 生成策略备份文件
                File backupFile = generationStrategyFile(fileDirectory);
                if (!backupFile.exists() || backupFile.isDirectory()) {
                    LOGGER.warn("备份文件路径无效：" + fileDirectory.getAbsolutePath());
                    return false;
                }
                // 执行备份文件命令
                if (executeBackupCmd(backupFile)) {
                    LOGGER.info("备份数据库成功");
                }
                return true;
            } catch (IOException | InterruptedException exception) {
                LOGGER.error("备份数据库失败 : {}" + exception.getMessage());
                return false;
            }
        } else {
            LOGGER.warn("目录：" + fileDirectory + " 不存在");
        }
        return false;
    }

    /**
     * 执行备份文件命令
     *
     * @return stream
     */
    private static boolean executeBackupCmd(File backupFile) throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime().exec(MYSQL_DUMP);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(exec.getInputStream());
        if (writeBackupFile(bufferedInputStream, backupFile)) {
            LOGGER.warn("写出备份文件失败");
            return false;
        }
        return exec.waitFor() == 0;
    }

    /**
     * 写入备份文件
     *
     * @date 2021-03-21 14:17:22
     * @param bufferedReader 缓冲流
     * @param backupFile 备份文件
     * @return boolean
     */
    private static boolean writeBackupFile(BufferedInputStream bufferedReader, File backupFile)  {
        if (bufferedReader == null) {
            LOGGER.warn("备份文件命令无效：" + MYSQL_DUMP);
            return true;
        }
        try (bufferedReader; BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(backupFile))) {
            byte[] data = new byte[16*1024];
            int len;
            while ((len = bufferedReader.read(data)) != -1) {
                outputStream.write(data, 0, len);
            }
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }


    /**
     * @param sql 包含待执行的SQL语句的ArrayList集合
     * @return int 影响的函数
     */
    private static int batchDate(List<String> sql) {
        try (Statement st = DbConnectionUtil.createConnection().createStatement()){
            for (String subSql : sql) {
                st.addBatch(subSql);
            }
            st.executeBatch();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 根据策略生成备份文件
     *
     * @param directory 文件目录
     * @return file
     */
    private static File generationStrategyFile(File directory) throws IOException {
        File file = Paths.get(directory.getAbsolutePath(), DATE_FORMAT.format(new Date()) + ".sql").toFile();
        if (file.isFile()) {
            throw new UnsupportedOperationException("file path is not supported");
        }
        if (!file.createNewFile()) {
            throw new RuntimeException("failed to create file");
        }
        return file;
    }



    /**
     * 以行为单位读取文件，并将文件的每一行格式化到ArrayList中，常用于读面向行的格式化文件
     */
    private static List<String> readFileByLines(InputStream inputStream) throws Exception {
        List<String> sqlList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String tempSql;
            int flag = 0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempSql = reader.readLine()) != null) {
                // 非空白继续执行
                if (!Objects.equals(BLANK, tempSql.trim())) {
                    flag = getFlag(sqlList, sb, tempSql, flag);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return sqlList;
    }


    /**
     * 读取文件内容到SQL中执行
     *
     * @param sqlPath SQL文件的路径：如：D:/TestProject/web/sql/脚本.Sql
     */
    public static Boolean runSqlByReadFileContent(InputStream sqlPath) {
        try {
            List<String> sqlStr = readFileByLines(sqlPath);
            if (sqlStr.size() > 0) {
                int num = batchDate(sqlStr);
                if (num > 0) {
                    System.out.println("execute complete...");
                    return false;
                } else{
                    System.out.println("no execute sql...");
                    return true;
                }
            } else {
                System.out.println("no execute sql...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getFlag(List<String> sqlList, StringBuffer sb, String tempSql, int flag) {
        if (INDEX_END.equals(tempSql.substring(tempSql.length() - 1))) {
            if (flag == 1) {
                sb.append(tempSql);
                sqlList.add(sb.toString());
                sb.delete(0, sb.length());
                flag = 0;
            } else {
                sqlList.add(tempSql);
            }
        } else {
            flag = 1;
            sb.append(tempSql);
        }
        return flag;
    }

}
