package utils;

import java.sql.*;

/**
 * Created by Asher on 2016/12/9.
 */
public class JDBCUtils {
    //加载驱动类
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //获取jdbc连接
    public static Connection getConnection(String host, int port, String dataBaseName, String userName, String password) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dataBaseName;
        try {
            return DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //释放资源
    public static void release(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                rs = null;
            }
        }

        if (st != null) try {
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            st = null;
        }

        if (conn != null) try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn = null;
        }
    }

}
