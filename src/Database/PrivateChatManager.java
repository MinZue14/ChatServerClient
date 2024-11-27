package Database;

import Object.PrivateMessage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrivateChatManager {

    // Gửi tin nhắn
    public boolean sendMessage(String sender, String receiver, String message, String filePath, boolean isEmoji) {
        int chatId = getNextChatId();  // Lấy ID chat tiếp theo

        String query = "INSERT INTO private_chats (chat_id, sender, receiver, message, file_path, is_emoji) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, chatId);
            stmt.setString(2, sender);
            stmt.setString(3, receiver);

            // Nếu là emoji, lưu emoji vào message
            if (isEmoji) {
                stmt.setString(4, message); // Gửi emoji
            } else {
                stmt.setString(4, message); // Gửi văn bản hoặc tên file
            }

            stmt.setString(5, filePath); // Nếu có file, đường dẫn file sẽ được lưu
            stmt.setBoolean(6, isEmoji); // Chỉ ra nếu là emoji

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private int getNextChatId() {
        String query = "SELECT MAX(chat_id) AS max_id FROM private_chats";
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

    // Lấy tất cả tin nhắn giữa hai người dùng
    public List<PrivateMessage> getMessages(String sender, String receiver) {
        List<PrivateMessage> messages = new ArrayList<>();
        String query;

        if (sender == null && receiver == null) {
            query = "SELECT * FROM private_chats ORDER BY timestamp";
        } else {
            query = "SELECT * FROM private_chats WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) ORDER BY timestamp";
        }

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            if (sender != null && receiver != null) {
                stmt.setString(1, sender);
                stmt.setString(2, receiver);
                stmt.setString(3, receiver);
                stmt.setString(4, sender);
            }

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                PrivateMessage message = new PrivateMessage(
                        resultSet.getInt("chat_id"),
                        resultSet.getString("sender"),
                        resultSet.getString("receiver"),
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

    // Xóa một tin nhắn theo chat_id
    public boolean deleteMessage(int chatId) {
        String query = "DELETE FROM private_chats WHERE chat_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, chatId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy chi tiết một tin nhắn cụ thể
    public String getMessageById(int chatId) {
        String query = "SELECT message, timestamp FROM private_chats WHERE chat_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, chatId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String message = resultSet.getString("message");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                return "[" + timestamp + "] " + message;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy lịch sử nhắn tin giữa hai người dùng
    public List<String> getChatHistory(String username1, String username2) {
        List<String> chatHistory = new ArrayList<>();
        String query = "SELECT sender, message, timestamp " +
                "FROM private_chats " +
                "WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) " +
                "ORDER BY timestamp ASC";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, username1);
            stmt.setString(2, username2);
            stmt.setString(3, username2);
            stmt.setString(4, username1);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String message = resultSet.getString("message");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                chatHistory.add("[" + timestamp + "] " + sender + ": " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatHistory;
    }
    public List<String> searchMessagesBetweenUsers(String username1, String username2, String keyword) {
        List<String> messages = new ArrayList<>();
        String query = "SELECT message, timestamp FROM private_chats " +
                "WHERE ((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)) " +
                "AND message LIKE ? ORDER BY timestamp";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(query)) {
            stmt.setString(1, username1);
            stmt.setString(2, username2);
            stmt.setString(3, username2);
            stmt.setString(4, username1);
            stmt.setString(5, "%" + keyword + "%");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String message = resultSet.getString("message");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                messages.add("[" + timestamp + "] " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    // Lấy tin nhắn với bộ lọc linh hoạt
    public List<PrivateMessage> getMessagesFiltered(String sender, String receiver, String content) {
        List<PrivateMessage> messages = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM private_chats WHERE 1=1");

        // Thêm các điều kiện lọc động
        if (sender != null && !sender.isEmpty()) {
            queryBuilder.append(" AND sender = ?");
        }
        if (receiver != null && !receiver.isEmpty()) {
            queryBuilder.append(" AND receiver = ?");
        }
        if (content != null && !content.isEmpty()) {
            queryBuilder.append(" AND message LIKE ?");
        }

        queryBuilder.append(" ORDER BY timestamp");

        try (PreparedStatement stmt = Database.getConnection().prepareStatement(queryBuilder.toString())) {
            int paramIndex = 1;

            // Gán giá trị cho các tham số động
            if (sender != null && !sender.isEmpty()) {
                stmt.setString(paramIndex++, sender);
            }
            if (receiver != null && !receiver.isEmpty()) {
                stmt.setString(paramIndex++, receiver);
            }
            if (content != null && !content.isEmpty()) {
                stmt.setString(paramIndex++, "%" + content + "%");
            }

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                PrivateMessage message = new PrivateMessage(
                        resultSet.getInt("chat_id"),
                        resultSet.getString("sender"),
                        resultSet.getString("receiver"),
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

}
