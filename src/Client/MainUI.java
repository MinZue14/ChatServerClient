package Client;

import Database.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI {
    private JFrame frame;
    private String username;
    private JTextField queryField;
    private JTextArea resultArea;
    private JButton accessButton;

    public MainUI(String username) {
        this.username = username;
        createUI();
    }

    private void createUI() {
        frame = new JFrame("Chat App Server - Client");
        frame.setSize(630, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(100, 149, 237));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Chào mừng đến với ứng dụng chat server - client", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel userLabel = new JLabel("Client: " + username);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(userLabel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel privateChat = new PrivateChat(username);
        tabbedPane.addTab("Chat Inbox", privateChat);

        JPanel groupChat = new GroupChat(username);
        tabbedPane.addTab("Chat Nhóm", groupChat);

        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(new Color(255, 182, 193));
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginRegisterUI();
        });
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutPanel.add(logoutButton);
        tabbedPane.addTab("Đăng Xuất", logoutPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createLogoutTab() {
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(new Color(255, 182, 193));
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginRegisterUI();
        });
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutPanel.add(logoutButton);
        return logoutPanel;
    }

    public static void main(String[] args) {
        new MainUI("Người dùng thử");
    }
}