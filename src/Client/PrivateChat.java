package Client;

import Object.User;
import Database.PrivateChatManager;
import Database.UserManager;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PrivateChat extends JPanel {
    private String username;
    private JTextPane chatArea;
    private JTextField messageField;
    private JPanel userListPanel;
    private UserManager userManager;
    private PrivateChatManager privateChatManager;
    private JLabel selectedUserLabel;
    private JButton refreshUserButton;
    private JLabel lastSelectedUserLabel = null;
    public PrivateChat(String username) {
        this.username = username;
        this.userManager = new UserManager();
        this.privateChatManager = new PrivateChatManager();
        setLayout(new BorderLayout());

        // Phía bên trái: Danh sách người dùng và nút làm mới người dùng
        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        userListPanel.setBackground(new Color(240, 240, 240));

        // Thêm JLabel "Danh sách người dùng" ở trên cùng của userListPanel
        JLabel userListLabel = new JLabel("Danh sách người dùng");
        userListLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userListLabel.setPreferredSize(new Dimension(150, 30));
        userListPanel.add(userListLabel);

        // Nút làm mới danh sách người dùng
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setPreferredSize(new Dimension(100, 30));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUserList(userManager.getAllUsers());
            }
        });

        // Thêm nút làm mới vào userListPanel dưới danh sách người dùng
        userListPanel.add(refreshButton);

        // Thêm các người dùng vào danh sách
        JScrollPane userScroll = new JScrollPane(userListPanel);
        userScroll.setPreferredSize(new Dimension(150, 0));
        add(userScroll, BorderLayout.WEST);

        // Phía bên phải: Khung chat
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        // Tên người dùng được chọn
        selectedUserLabel = new JLabel("Chat với: [Tên người dùng]");
        selectedUserLabel.setFont(new Font("Arial", Font.BOLD, 14));
        selectedUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
        chatPanel.add(selectedUserLabel, BorderLayout.NORTH);

        // Lịch sử chat
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatPanel.add(chatScroll, BorderLayout.CENTER);

        // Phía dưới: Khung nhập và các nút
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        // Các nút gửi và các icon
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Icon/clip.png"));
        Image resizedImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        JButton sendFileButton = new JButton(resizedIcon);
        sendFileButton.setPreferredSize(new Dimension(40, 40));
        sendFileButton.setToolTipText("Gửi File");

        ImageIcon originalEmojiIcon = new ImageIcon(getClass().getResource("/Icon/emoji.png"));
        Image resizedEmojiImage = originalEmojiIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon resizedEmojiIcon = new ImageIcon(resizedEmojiImage);
        JButton emojiButton = new JButton(resizedEmojiIcon);
        emojiButton.setPreferredSize(new Dimension(40, 40));
        emojiButton.setToolTipText("Chọn Emoji");

        ImageIcon originalSendIcon = new ImageIcon(getClass().getResource("/Icon/send_mess.png"));
        Image resizedSendImage = originalSendIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon resizedSendIcon = new ImageIcon(resizedSendImage);
        JButton sendButton = new JButton(resizedSendIcon);
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

        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.CENTER);

        // Sự kiện cho nút gửi tin nhắn
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Sự kiện cho nút gửi file
        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });

        // Sự kiện cho nút gửi emoji
        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendEmoji();
            }
        });

        // Cập nhật danh sách người dùng ban đầu
        updateUserList(userManager.getAllUsers());
    }

    // Hàm gửi tin nhắn
    private void sendMessage() {
        String message = messageField.getText();
        if (message != null && !message.trim().isEmpty()) {
            String selectedUser = selectedUserLabel.getText().replace("Chat với: ", "");
            if (privateChatManager.sendMessage(username, selectedUser, message, "", false)) {
                appendMessage("Bạn: " + message, Color.PINK); // Màu hồng cho người gửi
                displayChatHistory(selectedUser); // Cập nhật lại lịch sử chat sau khi gửi tin nhắn
            }
            messageField.setText("");
        }
    }

    // Hàm gửi file
// Hàm gửi file
    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String selectedUser = selectedUserLabel.getText().replace("Chat với: ", "");
            String filePath = file.getAbsolutePath();

            // Hiển thị tiến trình gửi file
            SwingWorker<Void, Void> fileSendWorker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    String message = "Đã gửi file: " + file.getName();
                    if (privateChatManager.sendMessage(username, selectedUser, message, filePath, false)) {
                        appendMessage("Bạn đã gửi file: " + file.getName(), Color.PINK);
                        displayChatHistory(selectedUser);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    JOptionPane.showMessageDialog(null, "Gửi file thành công!");
                }
            };
            fileSendWorker.execute();
        }
    }

    // Hàm gửi emoji
    private void sendEmoji() {
        // Tạo danh sách emoji
        String[] emojis = {"😊", "😂", "😍", "👍", "❤️", "😢"};
        String selectedEmoji = (String) JOptionPane.showInputDialog(
                this,
                "Chọn một emoji:",
                "Gửi Emoji",
                JOptionPane.PLAIN_MESSAGE,
                null,
                emojis,
                emojis[0]
        );

        if (selectedEmoji != null) {
            String selectedUser = selectedUserLabel.getText().replace("Chat với: ", "");
            // Gửi emoji như tin nhắn
            if (privateChatManager.sendMessage(username, selectedUser, selectedEmoji, null, true)) {
                appendMessage(username + " đã gửi emoji: " + selectedEmoji, Color.YELLOW);
                displayChatHistory(selectedUser);
            }
        }
    }

    // Hàm nhận tin nhắn
    public void receiveMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            if (message.startsWith("Đã gửi file: ")) {
                String fileName = message.replace("Đã gửi file: ", "");
                appendFileMessage("Người khác đã gửi file: " + fileName, Color.BLUE);
            } else if (message.startsWith("Emoji: ")) {
                String emoji = message.replace("Emoji: ", "");
                appendMessage("Người khác đã gửi emoji: " + emoji, Color.BLUE);
            } else {
                appendMessage("Người khác: " + message, Color.BLUE);
            }
            String selectedUser = selectedUserLabel.getText().replace("Chat với: ", "");
            displayChatHistory(selectedUser);
        }
    }

    // Hàm thêm liên kết file vào JTextPane
    private void appendFileMessage(String message, Color color) {
        StyledDocument doc = chatArea.getStyledDocument();
        Style style = chatArea.addStyle("Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), message + " ", style);
            JButton openFileButton = new JButton("Mở file");
            openFileButton.setForeground(Color.BLUE);
            openFileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            openFileButton.addActionListener(e -> {
                String filePath = message.replace("Người khác đã gửi file: ", "").trim();
                openFile(filePath);
            });
            chatArea.insertComponent(openFileButton);
            doc.insertString(doc.getLength(), "\n", null); // Xuống dòng sau nút
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm mở file
    private void openFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(null, "File không tồn tại hoặc không thể mở!");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi mở file!");
            e.printStackTrace();
        }
    }

    // Hàm thêm tin nhắn vào JTextPane với màu sắc
    private void appendMessage(String message, Color color) {
        StyledDocument doc = chatArea.getStyledDocument();
        Style style = chatArea.addStyle("Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), message + "\n", style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật lịch sử chat khi người dùng chọn người khác để chat
    private void displayChatHistory(String selectedUser) {
        List<String> chatHistory = privateChatManager.getChatHistory(username, selectedUser);
        chatArea.setText(""); // Xóa nội dung cũ trong chatArea
        for (String message : chatHistory) {
            appendMessage(message, Color.BLACK); // Hiển thị các tin nhắn trong lịch sử
        }
    }

    // Cập nhật danh sách người dùng trong giao diện
    private void updateUserList(List<User> users) {
        userListPanel.removeAll();

        // Thêm lại JLabel "Danh sách người dùng"
        JLabel userListLabel = new JLabel("Danh sách người dùng");
        userListLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userListLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userListLabel.setPreferredSize(new Dimension(150, 30));
        userListPanel.add(userListLabel);

        // Thêm các người dùng vào danh sách, loại trừ người dùng hiện tại
        for (User user : users) {
            if (!user.getUsername().equals(username)) {
                JLabel userLabel = new JLabel(user.getUsername());
                userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                userLabel.setPreferredSize(new Dimension(150, 30));
                userLabel.setHorizontalAlignment(SwingConstants.CENTER);

                userLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Hiển thị người dùng đã chọn
                        selectedUserLabel.setText("Chat với: " + user.getUsername());
                        displayChatHistory(user.getUsername());

                        // Đổi font của tên người dùng thành in đậm khi chọn
                        if (lastSelectedUserLabel != null) {
                            // Đặt lại font của người dùng trước đó về bình thường
                            lastSelectedUserLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        }

                        // Đặt font của người dùng hiện tại thành in đậm
                        userLabel.setFont(new Font("Arial", Font.BOLD, 14));

                        // Cập nhật lại người dùng đã chọn
                        lastSelectedUserLabel = userLabel;
                    }
                });

                userListPanel.add(userLabel);
            }
        }

        // Cập nhật lại giao diện
        userListPanel.revalidate();
        userListPanel.repaint();
    }

}
