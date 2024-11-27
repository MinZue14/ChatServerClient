package Server;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import Database.PrivateChatManager;

public class ChatPrivateServer implements Runnable {
    private ServerSocket serverSocket;
    private JTextPane logArea;
    private StyledDocument logDocument;
    private Map<String, ObjectOutputStream> clients;
    private PrivateChatManager chatManager;

    public ChatPrivateServer(JTextPane logArea) {
        this.logArea = logArea;
        this.logDocument = logArea.getStyledDocument();
        this.clients = new HashMap<>();
        this.chatManager = new PrivateChatManager();
        try {
            // Khởi tạo server socket trên một cổng cụ thể
            this.serverSocket = new ServerSocket(12346); // Cổng khác với server chính (12345)
            appendColoredLog("ChatPrivateServer đang chạy trên cổng 12346", Color.BLACK);
        } catch (IOException e) {
            appendColoredLog("Lỗi khi khởi động ChatPrivateServer: " + e.getMessage(), Color.RED);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                appendColoredLog("Kết nối từ " + clientSocket.getInetAddress(), Color.BLUE);

                // Xử lý mỗi client trên một luồng riêng
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            appendColoredLog("Lỗi trong vòng lặp ChatPrivateServer: " + e.getMessage(), Color.RED);
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                appendColoredLog("Lỗi khi đóng server socket: " + e.getMessage(), Color.RED);
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // Đọc tên người dùng từ client
                username = (String) in.readObject();
                synchronized (clients) {
                    clients.put(username, out);
                }
                appendColoredLog(username + " đã kết nối!", Color.GREEN);

                // Vòng lặp nhận tin nhắn từ client
                while (true) {
                    String recipient = (String) in.readObject();
                    String message = (String) in.readObject();
                    boolean isEmoji = in.readBoolean();
                    String filePath = (String) in.readObject();

                    // Lưu tin nhắn vào cơ sở dữ liệu
                    boolean success = chatManager.sendMessage(username, recipient, message, filePath, isEmoji);
                    if (success) {
                        sendMessageToRecipient(recipient, username, message, isEmoji, filePath);
                    } else {
                        appendColoredLog("Lỗi khi lưu tin nhắn từ " + username + " tới " + recipient, Color.RED);
                    }
                }
            } catch (Exception e) {
                appendColoredLog(username + " đã ngắt kết nối.", Color.ORANGE);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clients) {
                    clients.remove(username);
                }
            }
        }

        private void sendMessageToRecipient(String recipient, String sender, String message, boolean isEmoji, String filePath) {
            try {
                synchronized (clients) {
                    ObjectOutputStream recipientOut = clients.get(recipient);
                    if (recipientOut != null) {
                        recipientOut.writeObject(sender);
                        recipientOut.writeObject(message);
                        recipientOut.writeBoolean(isEmoji);
                        recipientOut.writeObject(filePath);
                        recipientOut.flush();

                        appendColoredLog("Tin nhắn từ " + sender + " tới " + recipient + ": " + message, Color.MAGENTA);
                    } else {
                        appendColoredLog("Không tìm thấy người nhận: " + recipient, Color.RED);
                    }
                }
            } catch (IOException e) {
                appendColoredLog("Lỗi khi gửi tin nhắn: " + e.getMessage(), Color.RED);
            }
        }
    }

    private void appendColoredLog(String log, Color color) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, color);

        try {
            logDocument.insertString(logDocument.getLength(), log + "\n", attributeSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
