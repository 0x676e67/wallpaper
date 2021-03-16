package com.zf1976.wallpaper.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 零依赖sql备份恢复工具
 * @author ant
 * Create by Ant on 2021/3/16 8:58 AM
 */
public class SqlBatchUtil {

    public static final String INDEX_END = ";";
    public static final String BLANK = "";
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

    public static void main(String[] args) {
        InputStream resourceAsStream = SqlBatchUtil.class.getClassLoader().getResourceAsStream("sql/backup/wallpaper.sql");
        if (runSqlByReadFileContent(resourceAsStream)) {
            System.out.println("execute complete...");
        }
    }
}
