package Server;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerUI {
    private JFrame frame;
    private JTextPane logArea;
    private StyledDocument logDocument;
    private ServerSocket serverSocket;
    private ChatPrivateServer chatPrivateServer;

    public ServerUI() {
        createUI();
        startServers(); // Khởi chạy cả ServerUI và ChatPrivateServer
    }

    private void createUI() {
        frame = new JFrame("Chat - Server Client");
        frame.setSize(600, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(92, 121, 171));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Chào mừng đến với ứng dụng chat Client - Server", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel userLabel = new JLabel("ADMIN");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(userLabel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel userManagerPanel = new UserManagerPanel();
        tabbedPane.addTab("Quản lý người dùng", userManagerPanel);

        JPanel chatPrivateManagerPanel = new ChatPrivateManagerPanel();
        tabbedPane.addTab("Quản lý tin nhắn riêng", chatPrivateManagerPanel);

        JPanel groupManagerPanel = new GroupManagerPanel();
        tabbedPane.addTab("Quản lý nhóm", groupManagerPanel);

        JPanel groupChatManagerPanel = new GroupChatManagerPanel();
        tabbedPane.addTab("Quản lý tin nhắn nhóm", groupChatManagerPanel);

        JPanel logPanel = createLogPanel();
        tabbedPane.addTab("Log", logPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel("Log hoạt động của server:");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(label, BorderLayout.NORTH);

        logArea = new JTextPane();
        logArea.setEditable(false);
        logDocument = logArea.getStyledDocument();

        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void appendColoredLog(String log, Color color) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, color); // Đặt màu cho text

        try {
            logDocument.insertString(logDocument.getLength(), log + "\n", attributeSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void startServers() {
        // Khởi chạy server chính
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(12345);
                appendColoredLog("Server đang chạy trên cổng 12345", Color.BLACK);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    appendColoredLog("Kết nối từ " + clientSocket.getInetAddress(), Color.BLUE);

                    // Tạo và chạy một ClientHandler
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                }
            } catch (IOException e) {
                appendColoredLog("Lỗi khi khởi động server: " + e.getMessage(), Color.RED);
            }
        }).start();

        // Khởi chạy ChatPrivateServer
        chatPrivateServer = new ChatPrivateServer(logArea);
        new Thread(chatPrivateServer).start();
    }

    public static void main(String[] args) {
        new ServerUI();
    }
}
