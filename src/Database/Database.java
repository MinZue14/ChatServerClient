package Database;
import java.sql.*;

public class Database {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/chatclientserver";
                String username = "root";
                String password = "";
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Kết nối cơ sở dữ liệu thành công!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
