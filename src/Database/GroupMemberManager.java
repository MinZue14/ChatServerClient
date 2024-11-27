package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberManager {

    // Thêm thành viên vào nhóm dựa trên group_name
    public boolean addMemberToGroup(String groupName, String username) {
        // Lấy group_id từ group_name
        int groupId = getGroupIdByName(groupName);
        if (groupId == -1) {
            System.out.println("Nhóm không tồn tại.");
            return false;
        }

        // Kiểm tra xem thành viên đã có trong nhóm chưa
        if (isMemberInGroup(groupId, username)) {
            System.out.println("Thành viên đã có trong nhóm.");
            return false;
        }

        String query = "INSERT INTO group_members (group_id, username) VALUES (?, ?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setString(2, username);
            stmt.executeUpdate();
            System.out.println("Thêm thành viên vào nhóm thành công.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy group_id từ group_name
    private int getGroupIdByName(String groupName) {
        String query = "SELECT group_id FROM groups WHERE group_name = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("group_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Nếu không tìm thấy nhóm
    }

    // Kiểm tra xem thành viên đã có trong nhóm chưa
    private boolean isMemberInGroup(int groupId, String username) {
        String query = "SELECT COUNT(*) FROM group_members WHERE group_id = ? AND username = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setString(2, username);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tất cả các nhóm mà thành viên tham gia
    public List<String> getGroupsByUsername(String username) {
        List<String> groups = new ArrayList<>();
        String query = "SELECT g.group_name FROM groups g " +
                "JOIN group_members gm ON g.group_id = gm.group_id " +
                "WHERE gm.username = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                groups.add(resultSet.getString("group_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    // Lấy danh sách thành viên của nhóm
    public List<String> getMembersByGroupName(String groupName) {
        List<String> members = new ArrayList<>();
        int groupId = getGroupIdByName(groupName);
        if (groupId == -1) {
            System.out.println("Nhóm không tồn tại.");
            return members;
        }

        String query = "SELECT username FROM group_members WHERE group_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                members.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    // Xóa thành viên khỏi nhóm
    public boolean removeMemberFromGroup(String groupName, String username) {
        int groupId = getGroupIdByName(groupName);
        if (groupId == -1) {
            System.out.println("Nhóm không tồn tại.");
            return false;
        }

        String query = "DELETE FROM group_members WHERE group_id = ? AND username = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setString(2, username);
            stmt.executeUpdate();
            System.out.println("Xóa thành viên khỏi nhóm thành công.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
