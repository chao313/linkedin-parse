package demo.spring.boot.demospringboot.parse.mysql.parse.db.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.UUID;
import java.util.function.Function;

/**
 * 内置的H2 database
 * maven
 * <dependency>
 * <groupId>com.h2database</groupId>
 * <artifactId>h2</artifactId>
 * <version>1.4.197</version>
 * </dependency>
 */

@Slf4j
@Component
public class MysqlBD {

    //数据库连接URL，当前连接的是当前项目的dbdir:下的mydb.mv.db
    private static String JDBC_URL;//jdbc:mysql://127.0.0.1:3306/dzr
    //账号
    private static String USER;//root
    //密码
    private static String PASSWORD;//123456
    //连接数据库时使用的驱动类
    private static String DRIVER_CLASS;//com.mysql.jdbc.Driver

    @Value("${spring.datasource.url}")
    public void setJdbcUrl(String JDBC_URL) {
        MysqlBD.JDBC_URL = JDBC_URL;
    }

    @Value("${spring.datasource.username}")
    public void setUSER(String USER) {
        MysqlBD.USER = USER;
    }

    @Value("${spring.datasource.password}")
    public void setPASSWORD(String PASSWORD) {
        MysqlBD.PASSWORD = PASSWORD;
    }

    @Value("${spring.datasource.driver-class-name}")
    public void setDriverClass(String DRIVER_CLASS) {
        MysqlBD.DRIVER_CLASS = DRIVER_CLASS;
    }

    public static MysqlBD h2BD;

    private MysqlBD() {

    }

    /**
     * 单例模式 -> 双重校验锁
     *
     * @return
     */
    public static MysqlBD getInstance(boolean proxyLog) {
        if (null == h2BD) {
            synchronized (MysqlBD.class) {
                if (null == h2BD) {
                    if (proxyLog) {
                        h2BD = new MysqlBD();
                    } else {
                        h2BD = ProxyModel.getInstance(new MysqlBD(), ((method, objects) -> {
                            Logger logger = LoggerFactory.getLogger(MysqlBD.class);
                            logger.info("args：{}", objects);
                            logger.info("method：{}", method);
                        }));
                    }
                }
            }
        }
        return h2BD;
    }

    /**
     * 使用泛型模板，处理返回集
     *
     * @param sql
     * @param function
     * @param <R>
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public <R> R executeQuery(String sql, Function<ResultSet, R> function)
            throws ClassNotFoundException, SQLException {
        //加载驱动
        Class.forName(DRIVER_CLASS);
        //根据连接URL，用户名，密码，获取数据库连接
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        //如果存在USER_INFO表就先删除USER_INFO表
        ResultSet resultSet = stmt.executeQuery(sql);
        R result = function.apply(resultSet);
        //释放资源
        stmt.close();
        //关闭连接
        conn.close();
        return result;
    }

    /**
     * 执行 添加-更新-删除 操作
     */
    public int executeInsertUpdateDelete(String sql)
            throws ClassNotFoundException, SQLException {
        //加载驱动
        Class.forName(DRIVER_CLASS);
        //根据连接URL，用户名，密码，获取数据库连接
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        //执行更新操作
        int result = stmt.executeUpdate(sql);
        //释放资源
        stmt.close();
        //关闭连接
        conn.close();
        return result;
    }

    /**
     * 执行sql -> 只返回执行成功与否
     */
    public boolean execute(String sql)
            throws ClassNotFoundException, SQLException {
        //加载驱动
        Class.forName(DRIVER_CLASS);
        //根据连接URL，用户名，密码，获取数据库连接
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        //执行更新操作
        boolean result = stmt.execute(sql);
        //释放资源
        stmt.close();
        //关闭连接
        conn.close();
        return result;
    }
}
