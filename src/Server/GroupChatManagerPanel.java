package Server;

import Database.GroupManager;
import Database.GroupChatManager;
import Database.GroupMemberManager;
import Object.MessageGroup;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GroupChatManagerPanel extends JPanel {

    private JComboBox<String> groupComboBox;
    private JTable chatTable;
    private JTable memberTable;
    private JTextArea messageArea;
    private JButton sendMessageButton;
    private JButton addMemberButton;
    private JButton removeMemberButton;

    private GroupManager groupManager;
    private GroupChatManager groupChatManager;
    private GroupMemberManager groupMemberManager;

    public GroupChatManagerPanel() {
        // Khởi tạo các manager
        groupManager = new GroupManager();
        groupChatManager = new GroupChatManager();
        groupMemberManager = new GroupMemberManager();

        // Thiết lập giao diện chính
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Panel trên cùng: Chọn nhóm
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel trung tâm: Tin nhắn và thành viên
        JSplitPane centerSplitPane = createCenterPanel();
        add(centerSplitPane, BorderLayout.CENTER);

        // Panel dưới cùng: Gửi tin nhắn và quản lý thành viên
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Tải danh sách nhóm ban đầu
        loadGroups();
    }

    // Tạo panel trên cùng
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(245, 245, 245));

        JLabel groupLabel = new JLabel("Nhóm:");
        groupComboBox = new JComboBox<>();
        groupComboBox.addActionListener(e -> {
            loadChatHistory();
            loadMembers();
        });

        JButton loadGroupsButton = new JButton("Tải danh sách nhóm");
        styleButton(loadGroupsButton, new Color(25, 118, 210));
        loadGroupsButton.addActionListener(e -> loadGroups());

        topPanel.add(groupLabel);
        topPanel.add(groupComboBox);
        topPanel.add(loadGroupsButton);

        return topPanel;
    }

    // Tạo panel trung tâm
    private JSplitPane createCenterPanel() {
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setResizeWeight(0.7);
        centerSplitPane.setDividerSize(5);

        // Bảng tin nhắn
        JPanel chatPanel = new JPanel(new BorderLayout(5, 5));
        chatPanel.setBorder(BorderFactory.createTitledBorder("Tin nhắn nhóm"));
        chatTable = new JTable(new DefaultTableModel(new Object[]{"Người gửi", "Tin nhắn", "Thời gian"}, 0));
        customizeTable(chatTable);
        chatTable.getTableHeader().setBackground(new Color(25, 118, 210));
        chatTable.getTableHeader().setForeground(Color.WHITE);
        chatTable.setSelectionBackground(new Color(173, 216, 230)); // Xanh nhạt khi chọn

        chatPanel.setBackground(new Color(240, 248, 255));
        chatPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
                "Quản Lý Tin Nhắn Nhóm",
                JLabel.CENTER,
                JLabel.CENTER,
                new Font("Arial", Font.BOLD, 16),
                new Color(25, 118, 210)
        ));

        chatPanel.add(new JScrollPane(chatTable), BorderLayout.CENTER);
        centerSplitPane.setLeftComponent(chatPanel);

        // Bảng thành viên
        JPanel memberPanel = new JPanel(new BorderLayout(5, 5));
        memberPanel.setBorder(BorderFactory.createTitledBorder("Danh sách thành viên"));

        memberTable = new JTable(new DefaultTableModel(new Object[]{"Tên thành viên"}, 0));
        customizeTable(memberTable);
        memberTable.getTableHeader().setBackground(new Color(25, 118, 210));
        memberTable.getTableHeader().setForeground(Color.WHITE);
        memberTable.setSelectionBackground(new Color(173, 216, 230)); // Xanh nhạt khi chọn
        memberPanel.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        centerSplitPane.setRightComponent(memberPanel);

        return centerSplitPane;
    }

    // Tạo panel dưới cùng
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(new Color(245, 245, 245));

        // Khu vực quản lý thành viên
        JPanel memberControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addMemberButton = new JButton("Thêm thành viên");
        removeMemberButton = new JButton("Xóa thành viên");
        styleButton(addMemberButton, new Color(0, 153, 76));
        styleButton(removeMemberButton, new Color(204, 0, 0));

        addMemberButton.addActionListener(e -> addMember());
        removeMemberButton.addActionListener(e -> removeMember());

        memberControlPanel.add(addMemberButton);
        memberControlPanel.add(removeMemberButton);
        bottomPanel.add(memberControlPanel, BorderLayout.SOUTH);

        return bottomPanel;
    }

    // Tùy chỉnh bảng
    private void customizeTable(JTable table) {
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // Định dạng nút
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
    }

    // Tải danh sách nhóm
    private void loadGroups() {
        groupComboBox.removeAllItems();
        List<String> groups = groupManager.getAllGroupNames();
        for (String group : groups) {
            groupComboBox.addItem(group);
        }
    }

    // Gửi tin nhắn
    private void sendMessage() {
        String groupName = (String) groupComboBox.getSelectedItem();
        if (groupName == null || messageArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhóm và nhập tin nhắn.");
            return;
        }

        boolean success = groupChatManager.sendMessageToGroup(groupName, "Admin", messageArea.getText().trim(), null, false);
        if (success) {
            JOptionPane.showMessageDialog(this, "Gửi tin nhắn thành công.");
            messageArea.setText("");
            loadChatHistory();
        } else {
            JOptionPane.showMessageDialog(this, "Gửi tin nhắn thất bại.");
        }
    }

    // Tải lịch sử chat
    private void loadChatHistory() {
        String groupName = (String) groupComboBox.getSelectedItem();
        if (groupName == null) return;

        List<MessageGroup> messages = groupChatManager.getChatHistoryByGroupName(groupName);
        DefaultTableModel model = (DefaultTableModel) chatTable.getModel();
        model.setRowCount(0);
        for (MessageGroup msg : messages) {
            model.addRow(new Object[]{msg.getSender(), msg.getContent(), msg.getTimestamp()});
        }
    }

    // Tải danh sách thành viên
    private void loadMembers() {
        String groupName = (String) groupComboBox.getSelectedItem();
        if (groupName == null) return;

        List<String> members = groupMemberManager.getMembersByGroupName(groupName);
        DefaultTableModel model = (DefaultTableModel) memberTable.getModel();
        model.setRowCount(0);
        for (String member : members) {
            model.addRow(new Object[]{member});
        }
    }

    // Thêm thành viên
    private void addMember() {
        String groupName = (String) groupComboBox.getSelectedItem();
        String username = JOptionPane.showInputDialog(this, "Nhập tên thành viên:");
        if (groupName == null || username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhóm và nhập tên thành viên.");
            return;
        }

        boolean success = groupMemberManager.addMemberToGroup(groupName, username.trim());
        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm thành viên thành công.");
            loadMembers();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thành viên thất bại.");
        }
    }

    // Xóa thành viên
    private void removeMember() {
        String groupName = (String) groupComboBox.getSelectedItem();
        int selectedRow = memberTable.getSelectedRow();
        if (groupName == null || selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhóm và thành viên để xóa.");
            return;
        }

        String username = (String) memberTable.getValueAt(selectedRow, 0);
        boolean success = groupMemberManager.removeMemberFromGroup(groupName, username);
        if (success) {
            JOptionPane.showMessageDialog(this, "Xóa thành viên thành công.");
            loadMembers();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thành viên thất bại.");
        }
    }
}
