import java.io.*;
import java.net.*;

public class ProxyServer {
    public static void main(String[] args) throws IOException {
        int port = 9000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[ProxyServer] Listening on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            InputStream clientIn = clientSocket.getInputStream();
            OutputStream clientOut = clientSocket.getOutputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = clientIn.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
                if (baos.toString().contains("\r\n\r\n")) break; // headers received
            }

            String request = baos.toString();
            String firstLine = request.split("\\r\\n")[0];
            String[] parts = firstLine.split(" ");
            String url = parts[1];
            URL targetUrl = new URL(url);

            int port = targetUrl.getPort() == -1 ? 80 : targetUrl.getPort();
            Socket targetSocket = new Socket(targetUrl.getHost(), port);
            OutputStream targetOut = targetSocket.getOutputStream();
            InputStream targetIn = targetSocket.getInputStream();

            // Modify request to remove full URL from the first line
            String newRequest = request.replace(url, targetUrl.getFile());
            targetOut.write(newRequest.getBytes());
            targetOut.flush();

            // Pipe response back to client
            byte[] respBuf = new byte[8192];
            int len;
            while ((len = targetIn.read(respBuf)) != -1) {
                clientOut.write(respBuf, 0, len);
                clientOut.flush();
            }

            targetSocket.close();
            clientSocket.close();

        } catch (Exception e) {
            System.err.println("[ProxyServer] Error: " + e.getMessage());
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}