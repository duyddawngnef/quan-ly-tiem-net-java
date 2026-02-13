package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/quanlynet";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối thành công!");
        } catch (SQLException e) {
            System.out.println("Kết nối thất bại!");
            e.printStackTrace();
        }
        return conn;
    }
}
