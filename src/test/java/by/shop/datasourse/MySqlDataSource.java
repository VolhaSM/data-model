package by.shop.datasourse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySqlDataSource {

    static Properties properties = new Properties();

    static {
        properties.put("user", "root");
        properties.put("password", "root");
        properties.put("useSSL", "false");
        properties.put("serverTimezone", "UTC");
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String testUrl = "jdbc:mysql://localhost:3306/shop_test";

    public static Connection getTestConnection() throws SQLException {
        return DriverManager.getConnection(testUrl, properties);
    }
}
