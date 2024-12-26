package Object;

import java.io.Serializable;
import java.sql.Timestamp;

public class MessageGroup implements Serializable {

    private static final long serialVersionUID = 1L; // Nên thêm serialVersionUID để đảm bảo tính tương thích
    private String sender;
    private String content;
    private Timestamp timestamp;
    private String filePath;
    private boolean isEmoji;

    // Constructor
    public MessageGroup(String sender, String content, Timestamp timestamp, String filePath, boolean isEmoji) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.filePath = filePath;
        this.isEmoji = isEmoji;
    }

    // Getters and setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isEmoji() {
        return isEmoji;
    }

    public void setEmoji(boolean emoji) {
        isEmoji = emoji;
    }

    @Override
    public String toString() {
        return "Gửi bởi: " + sender +
                " | Thời gian: " + timestamp +
                " | Tin nhắn: " + content +
                (filePath != null ? " | File đính kèm: " + filePath : "") +
                (isEmoji ? " | Có emoji" : "");
    }

    public boolean isFileMessage() {
        return filePath != null && !filePath.isEmpty();
    }
}
