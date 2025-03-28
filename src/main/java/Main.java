import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  private static final int PORT = 6379;
  private static final int THREAD_POOL_SIZE = 10;

  public static void handleClient(Socket clientSocket) {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      OutputStream outputStream = clientSocket.getOutputStream();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        System.out.println(inputLine.toLowerCase());
        if ("ping".equals(inputLine.toLowerCase())) {
          outputStream.write("+PONG\r\n".getBytes());
        }
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        if (clientSocket != null) {
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }

  public static void main(String[] args) {
    // Creating a thread pool with 10 threads to handle concurrent connections
    ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    System.out.println("Logs from your program will appear here!");
    ServerSocket serverSocket = null; // server socket

    int port = 6379; // redis server default port
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);
      System.out.println("Redis server is listening on port " + port + serverSocket.toString());
      // Wait for connection from client.
      while (true) {
        Socket clientSocket = serverSocket.accept(); // client socket
        System.out.println("Client connected: " + clientSocket.getInetAddress());
        threadPool.execute(() -> handleClient(clientSocket));
      }

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
