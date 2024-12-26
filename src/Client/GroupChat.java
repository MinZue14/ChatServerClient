package Client;

import Object.MessageGroup;
import Database.GroupManager;
import Database.GroupChatManager;
import Database.GroupMemberManager;
import Object.Group;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class GroupChat extends JPanel {
    private String username;
    private JTextPane chatArea;
    private JTextField messageField;
    private JPanel groupListPanel;
    private JPanel memberListPanel;
    private JPanel groupChatPanel;
    private GroupChatManager groupChatManager;
    private GroupManager groupManager;
    GroupMemberManager groupMemberManager = new GroupMemberManager();
    private JLabel selectedGroupLabel;
    private JButton createGroupButton;
    private JButton refreshGroupButton;
    private JButton sendButton;
    private JButton sendFileButton;
    private JButton emojiButton;

    public GroupChat(String username, ChatClient chatClient) {
        this.username = username;
        this.groupChatManager = new GroupChatManager();
        this.groupManager = new GroupManager();
        setLayout(new BorderLayout()); // Sử dụng BorderLayout làm layout chính

        // Panel bên trái - Danh sách nhóm
        groupListPanel = new JPanel();
        groupListPanel.setLayout(new BoxLayout(groupListPanel, BoxLayout.Y_AXIS));
        groupListPanel.setBackground(new Color(240, 240, 240));

        JLabel groupListLabel = new JLabel("Danh sách nhóm");
        groupListLabel.setFont(new Font("Arial", Font.BOLD, 16));
        groupListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        groupListPanel.add(groupListLabel);

        JPanel buttonGroupPanel = new JPanel();
        buttonGroupPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Sắp xếp nút nằm ngang
        buttonGroupPanel.setBackground(new Color(240, 240, 240)); // Màu nền của panel

// Nút "Tạo nhóm"
        createGroupButton = new JButton("Tạo nhóm");
        createGroupButton.setPreferredSize(new Dimension(100, 30));
        createGroupButton.setBackground(new Color(0, 153, 204)); // Màu xanh dương nhạt
        createGroupButton.setForeground(Color.WHITE); // Màu chữ trắng
        createGroupButton.setFont(new Font("Arial", Font.BOLD, 12));

// Nút "Làm mới"
        refreshGroupButton = new JButton("Làm mới");
        refreshGroupButton.setPreferredSize(new Dimension(100, 30));
        refreshGroupButton.setBackground(new Color(0, 204, 102)); // Màu xanh lá nhạt
        refreshGroupButton.setForeground(Color.WHITE); // Màu chữ trắng
        refreshGroupButton.setFont(new Font("Arial", Font.BOLD, 12));

// Thêm các nút vào panel chứa nút
        buttonGroupPanel.add(createGroupButton);
        buttonGroupPanel.add(refreshGroupButton);

// Thêm buttonGroupPanel vào groupListPanel
        groupListPanel.add(Box.createVerticalStrut(10)); // Khoảng cách
        groupListPanel.add(buttonGroupPanel);


        JScrollPane groupScroll = new JScrollPane(groupListPanel);
        groupScroll.setPreferredSize(new Dimension(200, 0));

        // Panel bên phải - Danh sách thành viên
        memberListPanel = new JPanel();
        memberListPanel.setLayout(new BoxLayout(memberListPanel, BoxLayout.Y_AXIS));

        JLabel memberListLabel = new JLabel("Danh sách thành viên");
        memberListLabel.setFont(new Font("Arial", Font.BOLD, 16));
        memberListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        memberListPanel.add(memberListLabel);

        JScrollPane memberScroll = new JScrollPane(memberListPanel);
        memberScroll.setPreferredSize(new Dimension(200, 0));

        // SplitPane chia nhóm và thành viên
        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, groupScroll, memberScroll);
        leftSplitPane.setDividerLocation(300);
        leftSplitPane.setResizeWeight(0.5);

        // Panel trung tâm - Khung chat nhóm
        groupChatPanel = new JPanel();
        groupChatPanel.setLayout(new BorderLayout());

        selectedGroupLabel = new JLabel("Chat nhóm: [Tên nhóm]");
        selectedGroupLabel.setFont(new Font("Arial", Font.BOLD, 16));
        selectedGroupLabel.setHorizontalAlignment(SwingConstants.CENTER);
        groupChatPanel.add(selectedGroupLabel, BorderLayout.NORTH);

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        groupChatPanel.add(chatScroll, BorderLayout.CENTER);

        // Phía dưới: Khung nhập và các nút
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        // Các nút gửi và các icon
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Icon/clip.png"));
        Image resizedImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        sendFileButton = new JButton(resizedIcon);
        sendFileButton.setPreferredSize(new Dimension(40, 40));
        sendFileButton.setToolTipText("Gửi File");

        ImageIcon originalEmojiIcon = new ImageIcon(getClass().getResource("/Icon/emoji.png"));
        Image resizedEmojiImage = originalEmojiIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon resizedEmojiIcon = new ImageIcon(resizedEmojiImage);
        emojiButton = new JButton(resizedEmojiIcon);
        emojiButton.setPreferredSize(new Dimension(40, 40));
        emojiButton.setToolTipText("Chọn Emoji");

        ImageIcon originalSendIcon = new ImageIcon(getClass().getResource("/Icon/send_mess.png"));
        Image resizedSendImage = originalSendIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon resizedSendIcon = new ImageIcon(resizedSendImage);
        sendButton = new JButton(resizedSendIcon);
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setToolTipText("Gửi Tin Nhắn");

        // Tạo JPanel cho các nút
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(sendFileButton);
        buttonPanel.add(emojiButton);
        buttonPanel.add(sendButton);

        // Trường nhập văn bản
        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(300, 30));
        Font font = new Font("Arial", Font.PLAIN, 16);
        messageField.setFont(font);

        // Thêm các thành phần vào inputPanel
        inputPanel.add(buttonPanel, BorderLayout.WEST);
        inputPanel.add(messageField, BorderLayout.CENTER);

        groupChatPanel.add(inputPanel, BorderLayout.SOUTH);

        // Kết hợp SplitPane chính
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, groupChatPanel);
        mainSplitPane.setDividerLocation(250);
        mainSplitPane.setResizeWeight(0.3);

        add(mainSplitPane, BorderLayout.CENTER);

        createGroupButton.addActionListener(e -> {
            String groupName = JOptionPane.showInputDialog(this, "Nhập tên nhóm mới:", "Tạo nhóm", JOptionPane.PLAIN_MESSAGE);
            if (groupName != null && !groupName.trim().isEmpty()) {
                if (!groupManager.isGroupExist(groupName)) { // Kiểm tra nhóm đã tồn tại
                    Group newGroup = new Group(groupName);
                    if (groupManager.addGroup(newGroup)) {
                        JOptionPane.showMessageDialog(this, "Nhóm đã được tạo thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        refreshGroupList(); // Cập nhật lại danh sách nhóm
                    } else {
                        JOptionPane.showMessageDialog(this, "Không thể tạo nhóm. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Tên nhóm đã tồn tại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        refreshGroupButton.addActionListener(e -> refreshGroupList());

        refreshGroupList();
        setupSendButtonAction();
    }

    private void refreshGroupList() {
        groupListPanel.removeAll(); // Xóa nội dung cũ

        // Tiêu đề danh sách nhóm
        JLabel groupListLabel = new JLabel("Danh sách nhóm");
        groupListLabel.setFont(new Font("Arial", Font.BOLD, 16));
        groupListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        groupListPanel.add(groupListLabel);

        // Tạo panel con chứa nút "Tạo nhóm" và "Làm mới"
        JPanel buttonGroupPanel = new JPanel();
        buttonGroupPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonGroupPanel.setBackground(new Color(240, 240, 240)); // Màu nền của panel con

        // Cấu hình nút "Tạo nhóm"
        createGroupButton.setPreferredSize(new Dimension(100, 30));
        createGroupButton.setBackground(new Color(0, 153, 204)); // Màu xanh dương nhạt
        createGroupButton.setForeground(Color.WHITE); // Màu chữ trắng
        createGroupButton.setFont(new Font("Arial", Font.BOLD, 12));

        // Cấu hình nút "Làm mới"
        refreshGroupButton.setPreferredSize(new Dimension(100, 30));
        refreshGroupButton.setBackground(new Color(0, 204, 102)); // Màu xanh lá nhạt
        refreshGroupButton.setForeground(Color.WHITE); // Màu chữ trắng
        refreshGroupButton.setFont(new Font("Arial", Font.BOLD, 12));

        // Thêm nút vào panel con
        buttonGroupPanel.add(createGroupButton);
        buttonGroupPanel.add(refreshGroupButton);

        // Thêm panel con vào groupListPanel
        groupListPanel.add(Box.createVerticalStrut(10)); // Khoảng cách
        groupListPanel.add(buttonGroupPanel);

        // Lấy danh sách nhóm từ cơ sở dữ liệu
        List<String> groupNames = groupManager.getAllGroupNames();
        for (String groupName : groupNames) {
            JPanel groupPanel = new JPanel();
            groupPanel.setLayout(new BorderLayout());

            JLabel groupLabel = new JLabel(groupName);  // Hiển thị tên nhóm
            groupLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            groupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            groupLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    loadGroupChat(groupName);  // Chuyển đến chat nhóm khi nhấn vào tên nhóm
                }
            });

            // Panel bên phải: Nút "Rời nhóm" hoặc "Tham gia"
            // Nút "Rời nhóm" hoặc "Tham gia" với màu sắc và kích thước cố định
            JButton joinLeaveButton = new JButton(isUserInGroup(groupName) ? "Rời nhóm" : "Tham gia");
            joinLeaveButton.setPreferredSize(new Dimension(100, 30)); // Kích thước cố định
            joinLeaveButton.setFont(new Font("Arial", Font.BOLD, 12));
            joinLeaveButton.setForeground(Color.WHITE); // Màu chữ trắng
            if (isUserInGroup(groupName)) {
                joinLeaveButton.setBackground(new Color(51, 170, 255)); // Đổi sang màu "Rời nhóm"
            } else {
                joinLeaveButton.setBackground(new Color(190, 22, 92)); // Đổi sang màu "Tham gia"
            }

            // Thêm sự kiện thay đổi trạng thái khi bấm nút
            joinLeaveButton.addActionListener(e -> {
                if (isUserInGroup(groupName)) {
                    leaveGroup(groupName); // Người dùng rời nhóm
                    joinLeaveButton.setText("Tham gia");
                    joinLeaveButton.setBackground(new Color(51, 170, 255)); // Đổi sang màu "Rời nhóm"
                } else {
                    joinGroup(groupName); // Người dùng tham gia nhóm
                    joinLeaveButton.setText("Rời nhóm");
                    joinLeaveButton.setBackground(new Color(190, 22, 92)); // Đổi sang màu "Tham gia"
                }
            });

            // Thêm tên nhóm và nút vào groupPanel
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BorderLayout());
            rightPanel.add(joinLeaveButton, BorderLayout.EAST);

            groupPanel.add(groupLabel, BorderLayout.WEST);
            groupPanel.add(rightPanel, BorderLayout.EAST);

            groupListPanel.add(groupPanel);
        }

        groupListPanel.revalidate();  // Cập nhật lại giao diện
        groupListPanel.repaint();
    }

    // Kiểm tra người dùng đã tham gia nhóm chưa
    private boolean isUserInGroup(String groupName) {
        // Lấy danh sách các nhóm mà người dùng tham gia từ đối tượng GroupMemberManager
        List<String> userGroups = groupMemberManager.getGroupsByUsername(username);
        // Kiểm tra xem nhóm cần kiểm tra có trong danh sách nhóm của người dùng không
        return userGroups.contains(groupName);
    }

    // Tham gia nhóm
    private void joinGroup(String groupName) {
        // Thêm người dùng vào nhóm thông qua GroupMemberManager
        boolean success = groupMemberManager.addMemberToGroup(groupName, username);
        if (success) {
            JOptionPane.showMessageDialog(this, "Đã tham gia nhóm: " + groupName);
        } else {
            JOptionPane.showMessageDialog(this, "Không thể tham gia nhóm: " + groupName);
        }
    }

    // Rời nhóm
    private void leaveGroup(String groupName) {
        // Xóa người dùng khỏi nhóm thông qua GroupMemberManager
        boolean success = groupMemberManager.removeMemberFromGroup(groupName, username);
        if (success) {
            JOptionPane.showMessageDialog(this, "Đã rời nhóm: " + groupName);
        } else {
            JOptionPane.showMessageDialog(this, "Không thể rời nhóm: " + groupName);
        }
    }
    private void appendChatMessage(String sender, String message, boolean isFile) {
        StyledDocument doc = chatArea.getStyledDocument();

        try {
            // Style cho tên người gửi
            Style senderStyle = chatArea.addStyle("SenderStyle", null);
            StyleConstants.setBold(senderStyle, true);
            StyleConstants.setForeground(senderStyle, Color.BLUE);

            // Style cho tin nhắn thông thường
            Style messageStyle = chatArea.addStyle("MessageStyle", null);
            StyleConstants.setForeground(messageStyle, Color.BLACK);

            // Style cho file
            Style fileStyle = chatArea.addStyle("FileStyle", null);
            StyleConstants.setForeground(fileStyle, Color.RED);
            StyleConstants.setUnderline(fileStyle, true);

            // Thêm tên người gửi
            doc.insertString(doc.getLength(), sender + ": ", senderStyle);

            if (isFile) {
                // Hiển thị tên file
                String fileName = message.substring(message.lastIndexOf(File.separator) + 1);
                doc.insertString(doc.getLength(), fileName + " ", fileStyle);

                // Tạo hai tùy chọn: Mở và Tải về
                addFileOptions(doc, message, fileName);
            } else {
                // Thêm tin nhắn thông thường
                doc.insertString(doc.getLength(), message, messageStyle);
            }

            doc.insertString(doc.getLength(), "\n", null); // Xuống dòng
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addFileOptions(StyledDocument doc, String filePath, String fileName) throws Exception {
        // Style cho liên kết
        Style linkStyle = chatArea.addStyle("LinkStyle", null);
        StyleConstants.setForeground(linkStyle, Color.BLUE);
        StyleConstants.setUnderline(linkStyle, true);

        // Thêm các liên kết Mở và Tải về
        doc.insertString(doc.getLength(), "[Mở] ", linkStyle);
        doc.insertString(doc.getLength(), "[Tải về]", linkStyle);

        // Xử lý sự kiện chuột
        chatArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int offset = chatArea.viewToModel(e.getPoint());
                    String clickedText = doc.getText(offset, 5);

                    if ("[Mở]".equals(clickedText)) {
                        File file = new File(filePath);
                        if (file.exists()) {
                            Desktop.getDesktop().open(file);
                        } else {
                            JOptionPane.showMessageDialog(null, "File không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if ("[Tải về]".equals(clickedText)) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setSelectedFile(new File(fileName));
                        int result = fileChooser.showSaveDialog(null);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File saveFile = fileChooser.getSelectedFile();
                            Files.copy(new File(filePath).toPath(), saveFile.toPath());
                            JOptionPane.showMessageDialog(null, "File đã được tải về!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Lỗi khi xử lý file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Cập nhật phương thức loadGroupChat
    private void loadGroupChat(String groupName) {
        selectedGroupLabel.setText("Chat nhóm: " + groupName); // Cập nhật tiêu đề nhóm chat
        chatArea.setText(""); // Xóa nội dung chat cũ

        // Kiểm tra người dùng đã tham gia nhóm chưa
        if (!isUserInGroup(groupName)) {
            JOptionPane.showMessageDialog(this, "Bạn phải tham gia nhóm trước khi có thể chat!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return; // Dừng lại nếu chưa tham gia nhóm
        }

        // Hiển thị danh sách thành viên của nhóm
        refreshMemberList(groupName);

        // Lấy lịch sử tin nhắn của nhóm từ cơ sở dữ liệu
        List<MessageGroup> messages = groupChatManager.getChatHistoryByGroupName(groupName);

        // Hiển thị các tin nhắn trong chatArea
        for (MessageGroup message : messages) {
            appendChatMessage(
                    message.getSender(),                  // Người gửi
                    message.getFilePath() != null ? message.getFilePath() : message.getContent(), // Nội dung tin nhắn hoặc file
                    message.getFilePath() != null        // Đánh dấu nếu là file
            );
        }
    }
    // Hiển thị danh sách thành viên của nhóm
    private void refreshMemberList(String groupName) {
        memberListPanel.removeAll();  // Xóa danh sách cũ

        JLabel memberListLabel = new JLabel("Danh sách thành viên: " + groupName);
        memberListLabel.setFont(new Font("Arial", Font.BOLD, 14));
        memberListPanel.add(memberListLabel);

        // Lấy danh sách thành viên từ GroupMemberManager
        List<String> members = groupMemberManager.getMembersByGroupName(groupName);
        for (String member : members) {
            JLabel memberLabel = new JLabel(member);
            memberLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            memberListPanel.add(memberLabel);
        }

        memberListPanel.revalidate();  // Cập nhật giao diện
        memberListPanel.repaint();
    }

    // Cập nhật phương thức gửi tin nhắn
    private void setupSendButtonAction() {
        // Gửi tin nhắn văn bản
        sendButton.addActionListener(e -> {
            String groupName = selectedGroupLabel.getText().replace("Chat nhóm: ", "").trim();
            if (groupName.isEmpty() || !isUserInGroup(groupName)) {
                JOptionPane.showMessageDialog(this, "Bạn phải tham gia nhóm trước khi có thể gửi tin nhắn!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                boolean success = groupChatManager.sendMessageToGroup(groupName, username, message, null, false);
                if (success) {
                    appendChatMessage(username, message, false); // Tin nhắn thường
                    messageField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể gửi tin nhắn. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

// Gửi file
        sendFileButton.addActionListener(e -> {
            String groupName = selectedGroupLabel.getText().replace("Chat nhóm: ", "").trim();
            if (groupName.isEmpty() || !isUserInGroup(groupName)) {
                JOptionPane.showMessageDialog(this, "Bạn phải tham gia nhóm trước khi có thể gửi file!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                boolean success = groupChatManager.sendMessageToGroup(groupName, username, "[File: " + filePath + "]", filePath, true);
                if (success) {
                    appendChatMessage(username, filePath, true); // Tin nhắn là file
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể gửi file. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        emojiButton.addActionListener(e -> {
            String groupName = selectedGroupLabel.getText().replace("Chat nhóm: ", "").trim();
            if (groupName.isEmpty() || !isUserInGroup(groupName)) {
                JOptionPane.showMessageDialog(this, "Bạn phải tham gia nhóm trước khi có thể gửi emoji!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tạo danh sách emoji mẫu
            String[] emojis = {"😀", "😂", "😍", "😎", "😢", "😡", "👍", "🙏", "🎉"};
            String emoji = (String) JOptionPane.showInputDialog(
                    this,
                    "Chọn một emoji để gửi:",
                    "Chọn Emoji",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    emojis,
                    emojis[0]
            );

            if (emoji != null && !emoji.trim().isEmpty()) {
                // Gửi tin nhắn emoji đến nhóm
                boolean success = groupChatManager.sendMessageToGroup(groupName, username, emoji, null, false);
                if (success) {
                    // Cập nhật giao diện với emoji vừa gửi
                    appendChatMessage(username, emoji, false);
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể gửi emoji. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }
}