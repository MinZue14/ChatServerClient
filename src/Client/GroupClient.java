package Client;

import Server.GroupHandle;
import Object.MessageGroup;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.IOException;

public class GroupClient {

    private ObjectOutputStream outputStream;

    public GroupClient(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    // Gửi tin nhắn
    public void sendMessage(String groupName, String sender, String message, String filePath, boolean isEmoji) {
        try {
            // Gửi tin nhắn tới server thông qua GroupHandle
            GroupHandle groupHandle = new GroupHandle(outputStream);
            groupHandle.handleSendMessage(groupName, sender, message, filePath, isEmoji);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gửi file
    public void sendFile(String groupName, String sender, File file) {
        try {
            // Gửi file tới server thông qua GroupHandle
            String filePath = file.getAbsolutePath();
            GroupHandle groupHandle = new GroupHandle(outputStream);
            groupHandle.handleSendFile(groupName, sender, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gửi emoji
    public void sendEmoji(String groupName, String sender) {
        try {
            // Gửi emoji tới server thông qua GroupHandle
            GroupHandle groupHandle = new GroupHandle(outputStream);
            groupHandle.handleSendEmoji(groupName, sender);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
