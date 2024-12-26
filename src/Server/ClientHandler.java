package Server;

import Database.PrivateChatManager;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private ServerUI serverUI;
    private String username;

    public ClientHandler(Socket clientSocket, ServerUI serverUI) {
        this.clientSocket = clientSocket;
        this.serverUI = serverUI;
    }

    @Override
    public void run() {
        try {
            // Thiết lập các luồng vào ra cho kết nối client
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Xử lý các yêu cầu từ client
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                if (clientMessage.equals("TEXT_MESSAGE")) {
                    handleTextMessage();
                } else if (clientMessage.equals("FILE_TRANSFER")) {
                    handleFileTransfer();
                } else if (clientMessage.equals("EMOJI")) {
                    handleEmoji();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Đóng kết nối khi kết thúc, nhưng sau khi tất cả công việc đã hoàn thành
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    // Xử lý tin nhắn văn bản
    private void handleTextMessage() throws IOException {
        String sender = in.readLine();
        String recipient = in.readLine();
        String message = in.readLine();

        // Lưu tin nhắn vào cơ sở dữ liệu
        PrivateChatManager chatManager = new PrivateChatManager();
        boolean isSaved = chatManager.sendMessage(sender, recipient, message, null, false);

        if (isSaved) {
            // Log và gửi phản hồi lại cho client gửi
            serverUI.appendColoredLog("Tin nhắn từ " + sender + " tới " + recipient + ": " + message, Color.BLACK);
            out.println("SUCCESS");

            // Gửi lại tin nhắn tới client nhận
            sendMessageToRecipient(recipient, "TEXT_MESSAGE", sender, message);
        } else {
            out.println("FAILED");
        }
    }

    // Xử lý gửi file
    private void handleFileTransfer() throws IOException {
        String sender = in.readLine(); // Đọc tên người gửi
        String recipient = in.readLine(); // Đọc tên người nhận
        String fileName = in.readLine(); // Đọc tên tệp

        // Đảm bảo thư mục "received_files" tồn tại
        File directory = new File("src/received_files");  // Hoặc thư mục khác tùy theo yêu cầu
        if (!directory.exists()) {
            directory.mkdirs();  // Tạo thư mục nếu chưa tồn tại
        }

        // Tạo một đối tượng File để lưu tệp
        File file = new File(directory, fileName);

        // Sử dụng BufferedOutputStream để ghi tệp nhận từ client vào thư mục
        try (BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] buffer = new byte[1024];  // Sử dụng buffer để đọc dữ liệu
            int bytesRead;

            // Đọc dữ liệu từ socket và ghi vào tệp
            while ((bytesRead = clientSocket.getInputStream().read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
            }
            fileOut.flush();  // Đảm bảo tất cả dữ liệu được ghi vào tệp

            // Kiểm tra xem tệp đã được lưu thành công
            if (file.exists()) {
                System.out.println("Tệp đã được lưu: " + file.getAbsolutePath());
                out.println("SUCCESS");  // Gửi phản hồi thành công cho client
            } else {
                System.err.println("Lỗi khi lưu tệp.");
                out.println("ERROR");  // Gửi phản hồi lỗi cho client
            }

        } catch (IOException e) {
            e.printStackTrace();
            out.println("ERROR");  // Gửi phản hồi lỗi nếu có sự cố trong quá trình ghi tệp
        }

        // Gửi thông tin về tệp đã nhận tới client nhận (nếu cần)
        sendMessageToRecipient(recipient, "FILE_TRANSFER", sender, fileName);
    }


    // Xử lý gửi emoji
    private void handleEmoji() throws IOException {
        String sender = in.readLine();
        String recipient = in.readLine();
        String emoji = in.readLine();

        // Lưu emoji vào cơ sở dữ liệu
        PrivateChatManager chatManager = new PrivateChatManager();
        boolean isSaved = chatManager.sendMessage(sender, recipient, emoji, null, true);

        if (isSaved) {
            serverUI.appendColoredLog("Emoji từ " + sender + " tới " + recipient + ": " + emoji, Color.BLACK);
            out.println("SUCCESS");

            // Gửi lại emoji tới client nhận
            sendMessageToRecipient(recipient, "EMOJI", sender, emoji);
        } else {
            out.println("FAILED");
        }
    }

    // Gửi tin nhắn tới client nhận
    private void sendMessageToRecipient(String recipient, String messageType, String sender, String content) {
        ClientHandler recipientHandler = serverUI.getClientHandlerByUsername(recipient);
        if (recipientHandler != null) {
            recipientHandler.out.println(messageType);
            recipientHandler.out.println(sender);
            recipientHandler.out.println(content);
        }
    }
}
