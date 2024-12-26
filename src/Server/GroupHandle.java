package Server;

import Database.GroupChatManager;
import Object.MessageGroup;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;

public class GroupHandle {

    private GroupChatManager groupChatManager;
    private ObjectOutputStream outputStream;

    public GroupHandle(ObjectOutputStream outputStream) {
        this.groupChatManager = new GroupChatManager();
        this.outputStream = outputStream;
    }

    // Xử lý gửi tin nhắn
    public void handleSendMessage(String groupName, String sender, String message, String filePath, boolean isEmoji) {
        // Tạo đối tượng MessageGroup
        MessageGroup messageGroup = new MessageGroup(sender, message, new Timestamp(System.currentTimeMillis()), filePath, isEmoji);

        // Lưu tin nhắn vào cơ sở dữ liệu
        boolean isSuccess = groupChatManager.sendMessageToGroup(groupName, sender, message, filePath, isEmoji);

        // Nếu gửi thành công, gửi lại tin nhắn cho client
        if (isSuccess) {
            try {
                outputStream.writeObject(messageGroup);
                outputStream.flush();
                System.out.println("Tin nhắn đã được gửi và lưu vào CSDL.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Có lỗi khi lưu tin nhắn vào CSDL.");
        }
    }

    // Xử lý gửi file
    public void handleSendFile(String groupName, String sender, String filePath) {
        // Tạo đối tượng MessageGroup với file đính kèm
        MessageGroup messageGroup = new MessageGroup(sender, null, new Timestamp(System.currentTimeMillis()), filePath, false);

        // Lưu file vào cơ sở dữ liệu
        boolean isSuccess = groupChatManager.sendMessageToGroup(groupName, sender, null, filePath, false);

        // Nếu gửi thành công, gửi lại tin nhắn cho client
        if (isSuccess) {
            try {
                outputStream.writeObject(messageGroup);
                outputStream.flush();
                System.out.println("File đã được gửi và lưu vào CSDL.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Có lỗi khi lưu file vào CSDL.");
        }
    }

    // Xử lý gửi emoji
    public void handleSendEmoji(String groupName, String sender) {
        // Tạo đối tượng MessageGroup với emoji
        MessageGroup messageGroup = new MessageGroup(sender, null, new Timestamp(System.currentTimeMillis()), null, true);

        // Lưu emoji vào cơ sở dữ liệu
        boolean isSuccess = groupChatManager.sendMessageToGroup(groupName, sender, null, null, true);

        // Nếu gửi thành công, gửi lại tin nhắn cho client
        if (isSuccess) {
            try {
                outputStream.writeObject(messageGroup);
                outputStream.flush();
                System.out.println("Emoji đã được gửi và lưu vào CSDL.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Có lỗi khi lưu emoji vào CSDL.");
        }
    }
}
