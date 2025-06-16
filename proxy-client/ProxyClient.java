import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ProxyClient {

    private static final int LISTEN_PORT = 8080;
    private static final String SERVER_HOST = "proxy-server";
    private static final int SERVER_PORT = 9000;

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(LISTEN_PORT);
        System.out.println("Ship Proxy is listening on port " + LISTEN_PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            executor.submit(() -> handleClient(clientSocket));
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            Socket serverSocket = new Socket(SERVER_HOST, SERVER_PORT);

            // Get the streams outside the lambda to handle checked exceptions
            InputStream clientIn = clientSocket.getInputStream();
            OutputStream serverOut = serverSocket.getOutputStream();
            InputStream serverIn = serverSocket.getInputStream();
            OutputStream clientOut = clientSocket.getOutputStream();

            executor.submit(() -> forward(clientIn, serverOut));
            executor.submit(() -> forward(serverIn, clientOut));
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    private static void forward(InputStream in, OutputStream out) {
        byte[] buffer = new byte[8192];
        int bytesRead;
        try {
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush(); // ensure bytes are flushed quickly
            }
        } catch (IOException e) {
            // Either socket closed or error occurred — can ignore
        } finally {
            try {
                out.close();
            } catch (IOException ignored) {
            }
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }
}