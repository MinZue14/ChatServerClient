package Server;

import Database.GroupManager;
import Object.Group;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GroupManagerPanel extends JPanel {
    private GroupManager groupManager;
    private JTable groupTable;
    private DefaultTableModel tableModel;
    private JTextField groupNameField;

    public GroupManagerPanel() {
        this.groupManager = new GroupManager();
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255)); // Màu nền xanh nhạt

        // Tiêu đề chính
        JLabel titleLabel = new JLabel("Quản Lý Nhóm", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 118, 210)); // Màu xanh đậm
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Tạo bảng hiển thị danh sách nhóm
        String[] columnNames = {"ID Nhóm", "Tên Nhóm", "Thời Gian Tạo"};
        tableModel = new DefaultTableModel(columnNames, 0);
        groupTable = new JTable(tableModel);
        groupTable.setFont(new Font("Arial", Font.PLAIN, 14));
        groupTable.setRowHeight(25);
        groupTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        groupTable.getTableHeader().setBackground(new Color(25, 118, 210));
        groupTable.getTableHeader().setForeground(Color.WHITE);
        groupTable.setSelectionBackground(new Color(173, 216, 230)); // Xanh nhạt khi chọn
        groupTable.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(groupTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
                "Danh Sách Nhóm",
                JLabel.CENTER,
                JLabel.CENTER,
                new Font("Arial", Font.BOLD, 16),
                new Color(25, 118, 210)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // Tạo phần quản lý nhóm (thêm, xóa, làm mới)
        JPanel controlPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        controlPanel.setBackground(new Color(240, 248, 255));
        controlPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
                "Quản Lý Nhóm",
                JLabel.CENTER,
                JLabel.CENTER,
                new Font("Arial", Font.BOLD, 16),
                new Color(25, 118, 210)
        ));

        JLabel groupNameLabel = new JLabel("Tên Nhóm:");
        groupNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        groupNameField = new JTextField();
        groupNameField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton addGroupButton = new JButton("Thêm Nhóm");
        JButton deleteGroupButton = new JButton("Xóa Nhóm");
        JButton refreshButton = new JButton("Làm Mới");

        // Chỉnh style nút
        styleButton(addGroupButton, new Color(0, 153, 76));
        styleButton(deleteGroupButton, new Color(204, 0, 0));
        styleButton(refreshButton, new Color(102, 153, 255));

        controlPanel.add(groupNameLabel);
        controlPanel.add(groupNameField);
        controlPanel.add(addGroupButton);
        controlPanel.add(deleteGroupButton);
        controlPanel.add(new JLabel()); // Chừa khoảng trống
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.SOUTH);

        // Nạp danh sách nhóm ban đầu
        loadGroupData();

        // Sự kiện khi thêm nhóm
        addGroupButton.addActionListener(e -> addGroup());

        // Sự kiện khi xóa nhóm
        deleteGroupButton.addActionListener(e -> deleteSelectedGroup());

        // Sự kiện làm mới dữ liệu
        refreshButton.addActionListener(e -> loadGroupData());
    }

    // Tải dữ liệu nhóm
    private void loadGroupData() {
        List<Group> groups = groupManager.getAllGroups();
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        for (Group group : groups) {
            tableModel.addRow(new Object[]{group.getGroupId(), group.getGroupName(), group.getCreatedAt()});
        }
    }

    // Thêm nhóm mới
    private void addGroup() {
        String groupName = groupNameField.getText().trim();
        if (groupName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhóm không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (groupManager.isGroupExist(groupName)) {
            JOptionPane.showMessageDialog(this, "Nhóm này đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Group group = new Group(0, groupName, null); // ID sẽ được tự động tạo
        if (groupManager.addGroup(group)) {
            JOptionPane.showMessageDialog(this, "Thêm nhóm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadGroupData(); // Cập nhật lại danh sách nhóm
            groupNameField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhóm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Xóa nhóm đã chọn
    private void deleteSelectedGroup() {
        int selectedRow = groupTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một nhóm để xóa!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int groupId = (int) tableModel.getValueAt(selectedRow, 0); // Lấy ID nhóm từ bảng
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhóm này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (groupManager.deleteGroup(groupId)) {
                JOptionPane.showMessageDialog(this, "Xóa nhóm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadGroupData(); // Cập nhật lại danh sách nhóm
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhóm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Style cho các nút
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(backgroundColor.darker()));
    }
}
