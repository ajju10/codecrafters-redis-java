import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");
        ServerSocket serverSocket;
        int port = 6379;

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                Runnable r = () -> {
                    String res;
                    try {
                        res = new Request(clientSocket).parse();
                        clientSocket.getOutputStream().write(res.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                };
                new Thread(r).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}