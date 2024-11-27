package Object;

import java.sql.Timestamp;

public class Group {
    private int groupId;
    private String groupName;
    private Timestamp createdAt;

    // Constructor
    public Group(int groupId, String groupName, Timestamp createdAt) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.createdAt = createdAt;
    }

    public Group(String groupName) {
        this.groupName = groupName;
    }

    // Getter and Setter methods
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // toString method for better representation
    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
