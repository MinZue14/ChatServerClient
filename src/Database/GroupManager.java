package Database;

import Object.Group;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupManager {

    // Lấy ID nhóm tiếp theo
    private int getNextGroupId() {
        String query = "SELECT MAX(group_id) AS max_id FROM groups";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("max_id") + 1; // Tăng dần từ ID tối đa
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Nếu bảng trống, bắt đầu từ 1
    }

    // Tạo nhóm mới với ID thủ công
    public boolean addGroup(Group group) {
        int groupId = getNextGroupId(); // Lấy ID tiếp theo
        String query = "INSERT INTO groups (group_id, group_name) VALUES (?, ?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setString(2, group.getGroupName());
            stmt.executeUpdate();
            System.out.println("Nhóm đã được thêm: " + group.getGroupName() + " với ID: " + groupId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hiển thị toàn bộ dữ liệu của các nhóm
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        String query = "SELECT * FROM groups";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int groupId = resultSet.getInt("group_id");
                String groupName = resultSet.getString("group_name");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                groups.add(new Group(groupId, groupName, createdAt)); // Thêm Group vào danh sách
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    // Hiển thị tất cả tên nhóm (vẫn sử dụng List<String> nếu chỉ cần tên nhóm)
    public List<String> getAllGroupNames() {
        List<String> groupNames = new ArrayList<>();
        String query = "SELECT group_name FROM groups"; // Chỉ lấy tên nhóm
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                groupNames.add(resultSet.getString("group_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupNames;
    }

    // Kiểm tra xem nhóm đã tồn tại hay chưa
    public boolean isGroupExist(String groupName) {
        String query = "SELECT COUNT(*) FROM groups WHERE group_name = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa nhóm theo group_id
    public boolean deleteGroup(int groupId) {
        String query = "DELETE FROM groups WHERE group_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.executeUpdate();
            System.out.println("Đã xóa nhóm với group_id: " + groupId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật tên nhóm
    public boolean updateGroup(int groupId, String newGroupName) {
        String query = "UPDATE groups SET group_name = ? WHERE group_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, newGroupName);
            stmt.setInt(2, groupId);
            stmt.executeUpdate();
            System.out.println("Đã cập nhật tên nhóm thành công.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
