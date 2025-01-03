package Client;

import Object.User;
import Object.PrivateMessage;
import Database.PrivateChatManager;
import Database.UserManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class PrivateChat extends JPanel {
    private PrivateChatManager privateChatManager;
    private UserManager userManager;
    private JLabel selectedUserLabel;
    private JTextField messageField;
    private JPanel filePanel;
    private JPanel userListPanel; // Panel hiển thị danh sách người dùng
    private String username;

    private ChatClient chatClient;

    public PrivateChat(String username, ChatClient chatClient) {
        this.chatClient = chatClient;
        this.username = username;
        this.userManager = new UserManager();
        this.privateChatManager = new PrivateChatManager();
        setLayout(new BorderLayout());

        // Phía bên trái: Danh sách người dùng
        userListPanel = createUserListPanel();
        add(userListPanel, BorderLayout.WEST);

        // Phía bên phải: Khung chat
        JPanel chatPanel = new JPanel(new BorderLayout());

        // Tên người dùng được chọn
        selectedUserLabel = new JLabel("Chat với: [Tên người dùng]");
        selectedUserLabel.setFont(new Font("Arial", Font.BOLD, 14));
        selectedUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
        chatPanel.add(selectedUserLabel, BorderLayout.NORTH);

        // Panel để hiển thị file đã gửi
        filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
        chatPanel.add(new JScrollPane(filePanel), BorderLayout.CENTER);

        // Phía dưới: Nhập tin nhắn và nút gửi
        JPanel inputPanel = new JPanel(new BorderLayout());

        JButton sendFileButton = createIconButton("/Icon/clip.png", "Gửi File");
        JButton emojiButton = createIconButton("/Icon/emoji.png", "Chọn Emoji");
        JButton sendButton = createIconButton("/Icon/send_mess.png", "Gửi Tin Nhắn");

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(sendFileButton);
        buttonPanel.add(emojiButton);
        buttonPanel.add(sendButton);

        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(300, 30));
        messageField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(buttonPanel, BorderLayout.WEST);
        inputPanel.add(messageField, BorderLayout.CENTER);

        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        add(chatPanel, BorderLayout.CENTER);

        // Sự kiện nút
        sendButton.addActionListener(e -> {
            sendMessage();
        });
        sendFileButton.addActionListener(e -> sendFile());
        emojiButton.addActionListener(e -> {
            try {
                sendEmoji();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Cập nhật danh sách người dùng
        updateUserList();
    }

    private JPanel createUserListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));

        // Tiêu đề "Danh sách người dùng"
        JLabel userListLabel = new JLabel("Danh sách người dùng");
        userListLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(userListLabel);

        // Nút làm mới
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> updateUserList());
        panel.add(refreshButton);

        return panel;
    }

    private void updateUserList() {
        // Lấy danh sách tất cả người dùng từ UserManager
        java.util.List<User> users = userManager.getAllUsers();

        // Lấy tên người dùng hiện tại
        String currentUsername = username; // Sử dụng username đã truyền vào

        // Lọc danh sách để loại bỏ người dùng hiện tại
        users.removeIf(user -> user.getUsername().equals(currentUsername));

        // Xóa tất cả các thành phần hiện tại khỏi panel danh sách người dùng
        JPanel userListPanel = (JPanel) getComponent(0);
        userListPanel.removeAll();

        // Thêm tiêu đề "Danh sách người dùng"
        JLabel userListLabel = new JLabel("Danh sách người dùng");
        userListLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userListPanel.add(userListLabel);

        // Thêm nút "Làm mới"
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setPreferredSize(new Dimension(100, 30));
        refreshButton.addActionListener(e -> updateUserList());
        userListPanel.add(refreshButton);

        // Thêm danh sách người dùng
        for (User user : users) {
            JLabel userLabel = new JLabel(user.getUsername());
            userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            userLabel.setPreferredSize(new Dimension(120, 30));
            userLabel.setHorizontalAlignment(SwingConstants.LEFT);

            // Thêm sự kiện click vào JLabel
            userLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectUser(user, userLabel);
                }
            });

            userListPanel.add(userLabel);
        }

        // Làm mới lại giao diện
        userListPanel.revalidate();
        userListPanel.repaint();
    }

    private void selectUser(User user, JLabel userLabel) {
        // Đặt tất cả nhãn trở về font thường
        JPanel userListPanel = (JPanel) getComponent(0);
        for (Component component : userListPanel.getComponents()) {
            if (component instanceof JLabel && !((JLabel) component).getText().equals("Danh sách người dùng")) {
                component.setFont(new Font("Arial", Font.PLAIN, 14));
            }
        }

        // Đặt nhãn của người dùng được chọn in đậm
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Cập nhật nhãn "Chat với"
        selectedUserLabel.setText("Chat với: " + user.getUsername());
//        System.out.println("Đã chọn người dùng: " + user.getUsername());

        // Lấy lịch sử chat giữa người dùng hiện tại và người được chọn
        List<PrivateMessage> messages = privateChatManager.getMessages(username, user.getUsername());

        // Hiển thị lịch sử chat
        displayMessages(messages);
    }
    private JPanel createMessageLabel(PrivateMessage message) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());

        String timeStamp = message.getTimestamp().toString();

        String displayText;
        if (message.isEmoji()) {
            displayText = String.format("<html><b>%s</b>: <img src='/path/to/emoji.png' width='10' height='10'> [%s]</html>",
                    message.getSender(), timeStamp); // Adjust emoji path as necessary
        } else {
            displayText = String.format("<html><b>%s</b>: %s [%s]</html>",
                    message.getSender(), message.getMessage(), timeStamp);
        }

        // Create a JLabel for the message text
        JLabel label = new JLabel(displayText);
        label.setHorizontalAlignment(SwingConstants.LEFT);

        messagePanel.add(label, BorderLayout.WEST);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding around the message

        return messagePanel;
    }
    private JButton createIconButton(String iconPath, String tooltip) {
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
        Image resizedImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        return new JButton(new ImageIcon(resizedImage)) {{
            setToolTipText(tooltip);
            setPreferredSize(new Dimension(40, 40));
        }};
    }
    private void displayMessages(List<PrivateMessage> messages) {
        filePanel.removeAll();

        for (PrivateMessage message : messages) {
            if (message.getFilePath() != null && !message.getFilePath().isEmpty()) {
                displayFile(new File(message.getFilePath()));
            } else {
                JPanel messagePanel = createMessageLabel(message);
                filePanel.add(messagePanel);
            }
        }

        // Update the UI
        filePanel.revalidate();
        filePanel.repaint();
    }

    private void sendMessage() {
        String message = messageField.getText();
        String recipient = selectedUserLabel.getText().replace("Chat với: ", "");

        if (!message.isEmpty() && !recipient.isEmpty()) {
            // Gửi tin nhắn và nhận phản hồi từ server
            String response = chatClient.sendMessage(username, recipient, message);
            if (response.equals("SUCCESS")) {
                displaySentMessage(message);
            } else {
                JOptionPane.showMessageDialog(this, "Gửi tin nhắn thất bại");
            }
            messageField.setText(""); // Xóa trường nhập
        }
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String selectedUser = selectedUserLabel.getText().replace("Chat với: ", "");

            try {
                // Kiểm tra tệp có tồn tại không
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(this, "Tệp không tồn tại");
                    return;
                }

                // Gửi thông tin về tệp (người nhận, người gửi, và tên tệp)
                String response = chatClient.sendFile(username, selectedUser, file);

                // Kiểm tra phản hồi từ server
                if ("SUCCESS".equals(response)) {
                    displayFile(file);  // Hiển thị tệp trong UI
                    JOptionPane.showMessageDialog(this, "Gửi tệp thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Gửi tệp thất bại. Phản hồi từ server: " + response);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi gửi tệp: " + e.getMessage());
                e.printStackTrace();  // In chi tiết lỗi ra console
            }
        }
    }

    private void sendEmoji() throws IOException {
        String recipient = selectedUserLabel.getText().replace("Chat với: ", "").trim();

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
            // Gửi emoji và nhận phản hồi từ server
            String response = chatClient.sendEmoji(username, recipient, emoji);

            if (response.equals("ERROR")) {
                JOptionPane.showMessageDialog(this, "Gửi emoji thất bại");
            } else {
                // Hiển thị emoji từ phản hồi server
                displayEmoji(response);
            }
        }
    }


    private void displaySentMessage(String message) {
        // Khởi tạo đối tượng PrivateMessage với timestamp và các thông tin cần thiết
        PrivateMessage sentMessage = new PrivateMessage(
                0,  // chatId, bạn có thể thay đổi hoặc gán giá trị này tùy vào logic của bạn
                username,  // sender
                selectedUserLabel.getText().replace("Chat với: ", ""),  // receiver (được lấy từ giao diện)
                message,  // message
                new Timestamp(System.currentTimeMillis()),  // timestamp
                null,  // filePath (trong trường hợp không có file)
                false  // isEmoji (false nếu đây là tin nhắn văn bản)
        );

        // Tạo panel để hiển thị tin nhắn
        JPanel messagePanel = createMessageLabel(sentMessage);
        filePanel.add(messagePanel);

        // Cập nhật lại giao diện
        filePanel.revalidate();
        filePanel.repaint();
    }

    private void displayEmoji(String emoji) {
        // Khởi tạo đối tượng PrivateMessage với emoji và các thông tin cần thiết
        PrivateMessage emojiMessage = new PrivateMessage(
                0,  // chatId, bạn có thể thay đổi hoặc gán giá trị này tùy vào logic của bạn
                username,  // sender
                selectedUserLabel.getText().replace("Chat với: ", ""),  // receiver
                emoji,  // message
                new Timestamp(System.currentTimeMillis()),  // timestamp
                null,  // filePath (trong trường hợp không có file)
                true  // isEmoji (true nếu đây là emoji)
        );

        // Tạo panel để hiển thị emoji
        JPanel emojiPanel = createMessageLabel(emojiMessage);
        filePanel.add(emojiPanel);

        // Cập nhật lại giao diện
        filePanel.revalidate();
        filePanel.repaint();
    }
    private void displayFile(File file) {
        JPanel fileEntry = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fileNameLabel = new JLabel(file.getName());
        JButton downloadButton = new JButton("Tải về");
        downloadButton.addActionListener(e -> downloadFile(file));
        fileEntry.add(fileNameLabel);
        fileEntry.add(downloadButton);
        filePanel.add(fileEntry);

        // Cập nhật lại giao diện
        filePanel.revalidate();
        filePanel.repaint();
    }
    private void downloadFile(File file) {
        try {
            // Định nghĩa hành động tải tệp (ví dụ: mở tệp, tải từ server, hoặc sao chép tệp vào thư mục)
            Desktop.getDesktop().open(file);  // Mở tệp trực tiếp
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải tệp: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}