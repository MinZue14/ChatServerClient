package Server;

import Database.PrivateChatManager;
import Object.PrivateMessage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ChatPrivateManagerPanel extends JPanel {
    private JTable messageTable;
    private DefaultTableModel tableModel;
    private JTextField filterSenderField;
    private JTextField filterReceiverField;
    private JTextField filterContentField; // Lọc theo nội dung
    private JButton filterButton, clearFilterButton, deleteButton, refreshButton;

    private PrivateChatManager privateChatManager;

    public ChatPrivateManagerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 248, 255)); // Màu nền xanh nhạt

        // Tiêu đề chính
        JLabel titleLabel = new JLabel("Quản Lý Tin Nhắn Riêng", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 118, 210)); // Màu xanh dương đậm
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Bảng hiển thị tin nhắn
        String[] columnNames = {"ID", "Người Gửi", "Người Nhận", "Nội Dung", "Thời Gian"};
        tableModel = new DefaultTableModel(columnNames, 0);
        messageTable = new JTable(tableModel);
        messageTable.setFont(new Font("Arial", Font.PLAIN, 14));
        messageTable.setRowHeight(25);
        messageTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        messageTable.getTableHeader().setBackground(new Color(25, 118, 210));
        messageTable.getTableHeader().setForeground(Color.WHITE);
        messageTable.setSelectionBackground(new Color(173, 216, 230)); // Xanh nhạt khi chọn
        messageTable.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(messageTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
                "Danh Sách Tin Nhắn",
                JLabel.CENTER,
                JLabel.CENTER,
                new Font("Arial", Font.BOLD, 16),
                new Color(25, 118, 210)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // Khu vực lọc tin nhắn
        JPanel filterPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        filterPanel.setBackground(new Color(240, 248, 255));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
                "Lọc Tin Nhắn",
                JLabel.CENTER,
                JLabel.CENTER,
                new Font("Arial", Font.BOLD, 16),
                new Color(25, 118, 210)
        ));

        JLabel senderLabel = new JLabel("Người Gửi:");
        JLabel receiverLabel = new JLabel("Người Nhận:");
        JLabel contentLabel = new JLabel("Nội Dung:");
        senderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        receiverLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        filterSenderField = new JTextField();
        filterReceiverField = new JTextField();
        filterContentField = new JTextField();

        filterButton = createStyledButton("Lọc", new Color(102, 153, 255));
        clearFilterButton = createStyledButton("Xóa Bộ Lọc", new Color(204, 0, 0));

        filterButton.addActionListener(new FilterAction());
        clearFilterButton.addActionListener(e -> {
            filterSenderField.setText("");
            filterReceiverField.setText("");
            filterContentField.setText("");
            loadMessages(null, null, null); // Hiển thị tất cả tin nhắn
        });

        filterPanel.add(senderLabel);
        filterPanel.add(filterSenderField);
        filterPanel.add(receiverLabel);
        filterPanel.add(filterReceiverField);
        filterPanel.add(contentLabel);
        filterPanel.add(filterContentField);
        filterPanel.add(filterButton);
        filterPanel.add(clearFilterButton);

        add(filterPanel, BorderLayout.NORTH);

        // Khu vực nút thao tác
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        deleteButton = createStyledButton("Xóa Tin Nhắn", new Color(204, 0, 0));
        refreshButton = createStyledButton("Làm Mới", new Color(0, 153, 76));

        deleteButton.addActionListener(new DeleteMessageAction());
        refreshButton.addActionListener(new RefreshAction());

        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Khởi tạo PrivateChatManager
        privateChatManager = new PrivateChatManager();
        loadMessages(null, null, null);
    }

    // Hành động lọc tin nhắn
    private class FilterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String sender = filterSenderField.getText().trim();
            String receiver = filterReceiverField.getText().trim();
            String content = filterContentField.getText().trim();
            loadMessages(sender, receiver, content);
        }
    }

    // Hành động xóa tin nhắn
    private class DeleteMessageAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = messageTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một tin nhắn để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int messageId = (int) tableModel.getValueAt(selectedRow, 0); // Lấy ID từ bảng
            boolean isDeleted = privateChatManager.deleteMessage(messageId);

            if (isDeleted) {
                JOptionPane.showMessageDialog(null, "Tin nhắn đã được xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadMessages(null, null, null); // Làm mới bảng
            } else {
                JOptionPane.showMessageDialog(null, "Không thể xóa tin nhắn.", "Thông báo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Hành động làm mới bảng tin nhắn
    private class RefreshAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadMessages(null, null, null); // Tải lại toàn bộ tin nhắn
        }
    }

    // Tải tin nhắn từ cơ sở dữ liệu
// Tải tin nhắn từ cơ sở dữ liệu
    private void loadMessages(String sender, String receiver, String content) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ

        List<PrivateMessage> messages = privateChatManager.getMessagesFiltered(sender, receiver, content);

        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy tin nhắn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (PrivateMessage message : messages) {
            Object[] row = {
                    message.getChatId(),
                    message.getSender(),
                    message.getReceiver(),
                    message.getMessage(),
                    message.getTimestamp()
            };
            tableModel.addRow(row);
        }
    }

    // Hàm tạo nút được định dạng đẹp
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(backgroundColor.darker()));
        return button;
    }
}
