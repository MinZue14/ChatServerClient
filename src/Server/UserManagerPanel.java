package Server;

import Object.User;
import Database.UserManager;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserManagerPanel extends JPanel {
    private JTextField userNameField;
    private JLabel resultLabel;
    private JButton addButton, updateButton, refreshButton, deleteButton;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagerPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Quản lý Người Dùng", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBackground(new Color(230, 230, 250));
        add(controlPanel, BorderLayout.WEST);

        // Khu vực nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        inputPanel.setBackground(new Color(230, 230, 250));
        inputPanel.setBorder(new TitledBorder("Nhập thông tin người dùng"));

        userNameField = new JTextField();
        inputPanel.add(userNameField);
        controlPanel.add(inputPanel, BorderLayout.NORTH);

        // Khu vực nút thao tác
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(new TitledBorder("Thao tác"));

        addButton = new JButton("Thêm người dùng");
        addButton.setBackground(new Color(0, 153, 76));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new AddUserAction());
        buttonPanel.add(addButton);

        updateButton = new JButton("Cập nhật người dùng");
        updateButton.setBackground(new Color(0, 102, 204));
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(new UpdateUserAction());
        buttonPanel.add(updateButton);

        refreshButton = new JButton("Làm mới người dùng");
        refreshButton.setBackground(new Color(102, 153, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(new RefreshAction());
        buttonPanel.add(refreshButton);

        deleteButton = new JButton("Xóa người dùng");
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new DeleteUserAction());
        buttonPanel.add(deleteButton);

        controlPanel.add(buttonPanel, BorderLayout.CENTER);

        // Khu vực hiển thị kết quả phân giải
        resultLabel = new JLabel("Kết quả sẽ hiển thị ở đây", SwingConstants.CENTER);
        resultLabel.setForeground(new Color(204, 0, 0));
        resultLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        add(resultLabel, BorderLayout.SOUTH);

        // Bảng hiển thị người dùng
        String[] columnNames = {"ID", "Tên Người Dùng", "Mật Khẩu"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        userTable.getTableHeader().setBackground(new Color(25, 118, 210));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setSelectionBackground(new Color(173, 216, 230)); // Xanh nhạt khi chọn
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        loadAllUsers();
    }

    // Hành động thêm người dùng
    private class AddUserAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = userNameField.getText();
            if (username.isEmpty()) {
                resultLabel.setText("Tên người dùng không được để trống.");
                return;
            }
            UserManager userManager = new UserManager();
            if (userManager.accountExists(username)) {
                resultLabel.setText("Tài khoản đã tồn tại.");
            } else {
                if (userManager.addUser(username, "defaultPassword")) { // Mật khẩu mặc định
                    resultLabel.setText("Thêm người dùng thành công.");
                    loadAllUsers();
                } else {
                    resultLabel.setText("Lỗi khi thêm người dùng.");
                }
            }
        }
    }

    // Hành động cập nhật người dùng
    private class UpdateUserAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = userTable.getSelectedRow();

            // Kiểm tra nếu người dùng có chọn một hàng trong bảng
            if (selectedRow == -1) {
                resultLabel.setText("Vui lòng chọn người dùng từ bảng.");
                return;
            }

            // Lấy giá trị từ các ô trong hàng đã chọn
            String username = (String) userTable.getValueAt(selectedRow, 1); // Cột tên người dùng
            String newPassword = (String) userTable.getValueAt(selectedRow, 2); // Cột mật khẩu

            // Kiểm tra xem tên người dùng có hợp lệ không
            if (username == null || username.isEmpty()) {
                resultLabel.setText("Tên người dùng không hợp lệ.");
                return;
            }

            // Cập nhật thông tin người dùng
            UserManager userManager = new UserManager();
            if (userManager.accountExists(username)) {
                if (userManager.updateUser(username, newPassword)) { // Cập nhật mật khẩu mới
                    resultLabel.setText("Cập nhật người dùng thành công.");
                    loadAllUsers(); // Làm mới bảng
                } else {
                    resultLabel.setText("Lỗi khi cập nhật người dùng.");
                }
            } else {
                resultLabel.setText("Tài khoản không tồn tại.");
            }
        }
    }

    // Hành động làm mới bảng người dùng
    private class RefreshAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadAllUsers();
            resultLabel.setText("Dữ liệu đã được làm mới.");
        }
    }

    // Hành động xóa người dùng
    private class DeleteUserAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = userNameField.getText();
            if (username.isEmpty()) {
                resultLabel.setText("Tên người dùng không được để trống.");
                return;
            }
            UserManager userManager = new UserManager();
            if (userManager.accountExists(username)) {
                if (userManager.deleteUser(username)) {
                    resultLabel.setText("Xóa người dùng thành công.");
                    loadAllUsers();
                } else {
                    resultLabel.setText("Lỗi khi xóa người dùng.");
                }
            } else {
                resultLabel.setText("Tài khoản không tồn tại.");
            }
        }
    }

    // Tải và hiển thị tất cả bản ghi người dùng trong bảng
    private void loadAllUsers() {
        UserManager userManager = new UserManager();
        java.util.List<User> users = userManager.getAllUsers(); // Assume this method returns a list of users

        // Clear the existing rows
        tableModel.setRowCount(0);

        // Add new rows from the users list
        for (User user : users) {
            Object[] row = {user.getId(), user.getUsername(), user.getPassword()};
            tableModel.addRow(row);
        }
    }
}
