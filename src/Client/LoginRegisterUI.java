package Client;

import Database.UserManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginRegisterUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserManager userManager = new UserManager();

    public LoginRegisterUI() {
        createUI();
    }

    private void createUI() {
        frame = new JFrame("DNS - Đăng nhập/Đăng ký");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel userLabel = new JLabel("Tên người dùng:");
        userLabel.setBounds(50, 50, 100, 25);
        frame.add(userLabel);
        usernameField = new JTextField(20);
        usernameField.setBounds(150, 50, 200, 25);
        frame.add(usernameField);

        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setBounds(50, 100, 100, 25);
        frame.add(passwordLabel);
        passwordField = new JPasswordField(20);
        passwordField.setBounds(150, 100, 200, 25);
        frame.add(passwordField);

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setBounds(50, 150, 100, 25);
        frame.add(loginButton);

        JButton registerButton = new JButton("Đăng ký");
        registerButton.setBounds(250, 150, 100, 25);
        frame.add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if ((username.isEmpty()) || (password.isEmpty())){
                    JOptionPane.showMessageDialog(frame, "Vui lòng nhập đầy đủ thông tin !");
                } else {
                    if (userManager.accountExists(username)) {
                        if (userManager.authenticateUser(username, password)) {
                            JOptionPane.showMessageDialog(frame, "Đăng nhập thành công!");
                            new MainUI(username);
                            frame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Sai tên đăng nhập hoặc mật khẩu.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Tài khoản không tồn tại.");
                    }

                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (userManager.accountExists(username)) {
                    JOptionPane.showMessageDialog(frame, "Tên đăng nhập đã tồn tại.");
                } else {
                    if (userManager.addUser(username, password)) {
                        JOptionPane.showMessageDialog(frame, "Đăng ký thành công!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Lỗi khi đăng ký tài khoản.");
                    }
                }
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new LoginRegisterUI();
    }
}