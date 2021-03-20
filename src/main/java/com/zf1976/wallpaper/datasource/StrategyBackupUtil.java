package com.zf1976.wallpaper.datasource;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 零依赖sql备份恢复工具
 * @author ant
 * Create by Ant on 2021/3/16 8:58 AM
 */
public class StrategyBackupUtil {

    private static final Logger LOGGER = Logger.getLogger(StrategyBackupUtil.class);
    public static final String INDEX_END = ";";
    public static final String BLANK = "";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String MYSQL_DUMP = "mysqldump --defaults-extra-file=/etc/my.cnf wallpaper";

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
                InputStream inputStream = executeBackupCmd();
                if (inputStream == null) {
                    LOGGER.warn("备份文件命令无效：" + MYSQL_DUMP);
                    return false;
                }
                if (!writeBackupFile(inputStream, backupFile)) {
                    LOGGER.warn("写出备份文件失败");
                    return false;
                }
                return true;
            } catch (IOException | InterruptedException exception) {
                LOGGER.error("备份数据库失败 : {}" + exception.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * 执行备份文件命令
     *
     * @return stream
     */
    private static InputStream executeBackupCmd() throws IOException, InterruptedException {
        Process exec = Runtime.getRuntime()
                              .exec(MYSQL_DUMP);
        if (exec.waitFor() == 0) {
            return exec.getInputStream();
        }
        return null;
    }

    private static boolean writeBackupFile(InputStream inputStream, File backupFile) throws IOException {
       try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(backupFile))){
           ;
           byte[] data = new byte[4*1024];
           int len;
           if ((len = inputStream.read(data)) != -1) {
               outputStream.write(data,0, len);
           }
           return true;
       } catch (IOException ignored) {
           return false;
       } finally {
           inputStream.close();
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
        File file = new File(directory.getAbsolutePath() + "/" + DATE_FORMAT.format(new Date()) + ".sql");
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
                if (num > 0){
                    System.out.println("execute complete...");
                    return false;
                } else{
                    System.out.println("no execute sql...");;
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

    public static void main(String[] args) throws IOException {
        InputStream dbResource = StrategyBackupUtil.class.getClassLoader().getResourceAsStream("sql/db.sql");
        if (runSqlByReadFileContent(dbResource)) {
            System.out.println("db create complete...");
        }
        if (generatedBackupFile(new File(System.getProperty("user.home")+"/desktop"))) {
            System.out.println();
        }
    }
}
