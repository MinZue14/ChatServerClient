package Server;

import Database.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    private final UserManager userManager;
    private final PrivateChatManager privateChatManager;
    private final GroupChatManager groupChatManager;
    private final GroupManager groupManager;
    private final GroupMemberManager groupMemberManager;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.userManager = new UserManager();
        this.privateChatManager = new PrivateChatManager();
        this.groupChatManager = new GroupChatManager();
        this.groupManager = new GroupManager();
        this.groupMemberManager = new GroupMemberManager();

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            System.err.println("Lỗi khi thiết lập kết nối với client: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Xác thực người dùng
            authenticateUser();

            // Lắng nghe và xử lý yêu cầu từ client
            String request;
            while ((request = reader.readLine()) != null) {
                handleRequest(request);
            }
        } catch (IOException e) {
            System.err.println("Lỗi trong quá trình giao tiếp: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void authenticateUser() throws IOException {
        writer.write("Vui lòng đăng nhập (username:password):\n");
        writer.flush();

        String credentials = reader.readLine();
        if (credentials != null) {
            String[] parts = credentials.split(":");
            if (parts.length == 2 && userManager.authenticateUser(parts[0], parts[1])) {
                username = parts[0];
                writer.write("Đăng nhập thành công!\n");
                writer.flush();
                return;
            }
        }
        writer.write("Đăng nhập thất bại! Đóng kết nối...\n");
        writer.flush();
        closeConnection();
    }

    private void handleRequest(String request) throws IOException {
        if (request.startsWith("MESSAGE")) {
            handleMessage(request);
        } else if (request.startsWith("SEND_FILE")) {
            handleFile(request);
        } else if (request.startsWith("EMOJI")) {
            handleEmoji(request);
        } else if (request.startsWith("GROUP_CHAT")) {
            handleGroupChat(request);
        } else {
            writer.write("Yêu cầu không hợp lệ!\n");
            writer.flush();
        }
    }

    private void handleMessage(String request) throws IOException {
        String[] parts = request.split(" ", 4);  // Chỉnh sửa để có thể nhận thêm filePath và isEmoji.
        if (parts.length >= 3) {
            String receiver = parts[1];
            String message = parts[2];
            String filePath = null;
            boolean isEmoji = false;

            // Nếu yêu cầu có file (được giả định là "SEND_FILE <file_path>"), xử lý phần này
            if (parts.length >= 4 && parts[2].equals("SEND_FILE")) {
                filePath = parts[3];  // Lấy đường dẫn file từ yêu cầu
                message = "";  // Không cần tin nhắn, chỉ gửi file
            }

            // Kiểm tra xem có phải gửi emoji hay không
            if (message.equals("EMOJI")) {
                isEmoji = true;
                message = "";  // Không cần tin nhắn nếu gửi emoji
            }

            boolean success = privateChatManager.sendMessage(username, receiver, message, filePath, isEmoji);
            if (success) {
                if (isEmoji) {
                    writer.write("Đã gửi emoji tới " + receiver + "\n");
                } else if (filePath != null) {
                    writer.write("Đã gửi file tới " + receiver + "\n");
                } else {
                    writer.write("Đã gửi tin nhắn tới " + receiver + "\n");
                }
            } else {
                writer.write("Không thể gửi tin nhắn tới " + receiver + "\n");
            }
        } else {
            writer.write("Cú pháp không hợp lệ! Định dạng: MESSAGE <receiver> <message> hoặc SEND_FILE <file_path>\n");
        }
        writer.flush();
    }

    private void handleFile(String request) throws IOException {
        writer.write("Nhận file...\n");
        writer.flush();
        // Giả lập xử lý file (cần bổ sung logic nếu yêu cầu cụ thể hơn)
    }

    private void handleEmoji(String request) throws IOException {
        writer.write("Gửi emoji...\n");
        writer.flush();
        // Giả lập xử lý emoji (cần bổ sung logic nếu yêu cầu cụ thể hơn)
    }

    private void handleGroupChat(String request) throws IOException {
        writer.write("Xử lý chat nhóm...\n");
        writer.flush();
        // Giả lập xử lý chat nhóm (cần bổ sung logic nếu yêu cầu cụ thể hơn)
    }

    private void closeConnection() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }
}
