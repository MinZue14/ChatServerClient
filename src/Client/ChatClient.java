package Client;

import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private DataOutputStream fileOut;
    private String serverAddress;
    private int serverPort;
    private String username;

    public ChatClient(String serverAddress, int serverPort, String username) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
    }

    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void listenForMessages() {
        new Thread(() -> {
            try {
                String messageType;
                while ((messageType = in.readLine()) != null) {
                    String sender = in.readLine();
                    String content = in.readLine();
                    // Xử lý tin nhắn từ server
                    if (messageType.equals("TEXT_MESSAGE")) {
                        displayReceivedMessage(content);
                    } else if (messageType.equals("FILE_TRANSFER")) {
                        displayFile(content);
                    } else if (messageType.equals("EMOJI")) {
                        displayEmoji(content);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void displayReceivedMessage(String message) {
        System.out.println("Nhận tin nhắn: " + message);
    }

    private void displayFile(String fileName) {
        System.out.println("Nhận file: " + fileName);
    }

    private void displayEmoji(String emoji) {
        System.out.println("Nhận emoji: " + emoji);
    }

    public String sendMessage(String sender, String recipient, String message) {
        try {
            if (out == null) {
                connect();
            }
            out.println("TEXT_MESSAGE");
            out.println(sender);
            out.println(recipient);
            out.println(message);

            String response = in.readLine();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public String sendFile(String sender, String recipient, File file) throws IOException {
        try {
            if (socket == null || socket.isClosed() || !socket.isConnected()) {
                System.out.println("Socket không hợp lệ. Đang cố gắng kết nối lại...");
                connect();  // Nếu socket không hợp lệ, kết nối lại
            } else {
                System.out.println("Socket hợp lệ, tiếp tục truyền tải dữ liệu.");
            }

            // Gửi thông tin về tệp
            out.println("FILE_TRANSFER");
            out.println(sender);
            out.println(recipient);
            out.println(file.getName());

            // Sử dụng DataOutputStream để gửi tệp
            try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file));
                 DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream())) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
                dataOut.flush();  // Đảm bảo dữ liệu được gửi
                System.out.println("Đã gửi tệp: " + file.getName());
            }

            // Đảm bảo dữ liệu được gửi
            if (out != null) {
                out.flush();  // Đảm bảo dữ liệu được gửi
                System.out.println("dữ liệu đã đc gửi.");
            } else {
                System.out.println("out là null, không thể gọi flush.");
                return "ERROR_OUT_NULL";
            }

            String response = null;

            System.out.println("Kiểm tra luồng vào:");
            if (in != null) {
                System.out.println("BufferedReader 'in' hợp lệ.");
            } else {
                System.out.println("BufferedReader 'in' là null.");
            }

//            try {
//                System.out.println("Đang đọc phản hồi từ server...");
//                response = in.readLine();
//                if (response == null) {
//                    System.out.println("Không nhận được phản hồi từ server.");
//                    return "ERROR_NO_RESPONSE";
//                }
//            } catch (IOException e) {
//                System.out.println("Lỗi khi đọc phản hồi từ server: " + e.getMessage());
//                e.printStackTrace();
//                return "ERROR_READING_RESPONSE";
//            }
            System.out.println("Đã nhận phản hồi: " + response);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public String sendEmoji(String sender, String recipient, String emoji) {
        try {
            if (out == null) {
                connect();
            }

            out.println("EMOJI");
            out.println(sender);
            out.println(recipient);
            out.println(emoji);  // Gửi emoji dưới dạng chuỗi ký tự

            String response = in.readLine();  // Nhận phản hồi từ server
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}
