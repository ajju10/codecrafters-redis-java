import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

//        ServerSocket serverSocket = null;
        int port = 6379;
        // Handling multiple clients will be implemented here
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                assert serverSocket != null;
                Socket clientSocket = serverSocket.accept();
                Runnable r = () -> {
                    try {
                        handleClientRequest(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
                Thread t = new Thread(r);
                System.out.println("Thread started: " + t.getName());
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleClientRequest(Socket socket) throws IOException {
        final String response = "+PONG\r\n";
        BufferedReader istream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter ostream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String line;
        while ((line = istream.readLine()) != null) {
            System.out.println("Line: " + line);
            if (line.equalsIgnoreCase("ping")) {
                ostream.write(response);
                ostream.flush();
            }
        }
    }
}
