package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class ChatClient {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton, fileButton, emojiButton;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ChatClient(String serverAddress, int port) {
        createUI();
        connectToServer(serverAddress, port);
    }

    private void createUI() {
        frame = new JFrame("Chat Client");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        fileButton = new JButton("File");
        emojiButton = new JButton("Emoji");

        inputPanel.add(messageField, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(sendButton);
        buttonPanel.add(fileButton);
        buttonPanel.add(emojiButton);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.SOUTH);
        frame.add(panel);

        frame.setVisible(true);

        // Sự kiện gửi tin nhắn
        sendButton.addActionListener(e -> sendMessage());
        // Sự kiện gửi file
        fileButton.addActionListener(e -> sendFile());
        // Sự kiện gửi emoji
        emojiButton.addActionListener(e -> sendEmoji());
    }

    private void connectToServer(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Lắng nghe từ server
            new Thread(this::listenFromServer).start();

            // Đăng ký tên người dùng
            String username = JOptionPane.showInputDialog(frame, "Enter your username:");
            if (username != null) {
                out.writeObject(username);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Unable to connect to server!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listenFromServer() {
        try {
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof String) {
                    chatArea.append(obj + "\n");
                } else if (obj instanceof byte[]) {
                    // Nhận file
                    receiveFile((byte[]) obj);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        try {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                out.writeObject(message);
                out.flush();
                messageField.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                byte[] fileData = Files.readAllBytes(file.toPath());
                out.writeObject(fileData);
                out.flush();
                chatArea.append("File sent: " + file.getName() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveFile(byte[] fileData) {
        try {
            File file = new File("received_file_" + System.currentTimeMillis());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileData);
            fos.close();

            chatArea.append("File received: " + file.getAbsolutePath() + "\n");
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendEmoji() {
        String[] emojis = {"😊", "😂", "👍", "❤️", "🔥"};
        String selectedEmoji = (String) JOptionPane.showInputDialog(
                frame,
                "Select an emoji:",
                "Emoji",
                JOptionPane.PLAIN_MESSAGE,
                null,
                emojis,
                emojis[0]
        );
        if (selectedEmoji != null) {
            try {
                out.writeObject(selectedEmoji);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatClient("localhost", 23456); // Kết nối tới server chat riêng
    }
}
