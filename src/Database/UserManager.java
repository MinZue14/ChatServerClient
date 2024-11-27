package Database;

import Object.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    public boolean accountExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next(); // Nếu có bản ghi thì tài khoản đã tồn tại
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next(); // Nếu có bản ghi khớp với username và password, xác thực thành công
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(String username, String password) {
        // Lấy id lớn nhất hiện tại
        int id = getNextId();

        String query = "INSERT INTO users (user_id, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.executeUpdate();
            return true; // Thêm người dùng thành công
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private int getNextId() {
        String query = "SELECT MAX(user_id) AS max_id FROM users";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Nếu bảng trống, bắt đầu từ 1
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, password FROM users";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                users.add(new User(id, username, password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean updateUser(String username, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(String username) {
        String query = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}