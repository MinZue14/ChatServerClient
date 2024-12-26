package Database;

import Object.MessageGroup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupChatManager {

    // Lấy message_id tiếp theo từ giá trị lớn nhất hiện tại trong group_chats
    private int getNextMessageId() {
        String query = "SELECT MAX(message_id) AS max_id FROM group_chats";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("max_id") + 1;  // Tăng 1 so với giá trị lớn nhất hiện tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;  // Nếu bảng trống, bắt đầu từ 1
    }

    // Gửi tin nhắn vào nhóm
    public boolean sendMessageToGroup(String groupName, String sender, String message, String filePath, boolean isEmoji) {
        int groupId = getGroupIdByName(groupName);
        if (groupId == -1) {
            System.out.println("Nhóm không tồn tại.");
            return false;
        }

        int messageId = getNextMessageId();  // Lấy message_id tiếp theo

        String query = "INSERT INTO group_chats (message_id, group_id, sender, message, file_path, is_emoji) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, messageId);  // Sử dụng message_id tự tăng
            stmt.setInt(2, groupId);
            stmt.setString(3, sender);
            stmt.setString(4, message);
            stmt.setString(5, filePath);
            stmt.setBoolean(6, isEmoji);
            stmt.executeUpdate();
            System.out.println("Tin nhắn đã được gửi.");
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

    // Lịch sử tin nhắn của nhóm
    public List<MessageGroup> getChatHistoryByGroupName(String groupName) {
        List<MessageGroup> messages = new ArrayList<>();
        int groupId = getGroupIdByName(groupName);
        if (groupId == -1) {
            System.out.println("Nhóm không tồn tại.");
            return messages;
        }

        String query = "SELECT sender, message, timestamp, file_path, is_emoji FROM group_chats WHERE group_id = ? ORDER BY timestamp ASC";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                MessageGroup message = new MessageGroup(
                        resultSet.getString("sender"),
                        resultSet.getString("message"),
                        resultSet.getTimestamp("timestamp"),
                        resultSet.getString("file_path"),
                        resultSet.getBoolean("is_emoji")
                );
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }


    // Tìm kiếm trong lịch sử tin nhắn
    public List<String> searchChatHistory(String groupName, String searchTerm) {
        List<String> results = new ArrayList<>();
        int groupId = getGroupIdByName(groupName);
        if (groupId == -1) {
            System.out.println("Nhóm không tồn tại.");
            return results;
        }

        String query = "SELECT sender, message, timestamp, file_path, is_emoji FROM group_chats " +
                "WHERE group_id = ? AND message LIKE ? ORDER BY timestamp ASC";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setString(2, "%" + searchTerm + "%");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String message = "Gửi bởi: " + resultSet.getString("sender") +
                        " | Thời gian: " + resultSet.getTimestamp("timestamp") +
                        " | Tin nhắn: " + resultSet.getString("message");
                if (resultSet.getString("file_path") != null) {
                    message += " | File đính kèm: " + resultSet.getString("file_path");
                }
                if (resultSet.getBoolean("is_emoji")) {
                    message += " | Có emoji";
                }
                results.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

}
