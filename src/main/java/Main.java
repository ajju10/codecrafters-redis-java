import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket;
        Socket clientSocket = null;
        int port = 6379;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            // Wait for connection from client.
            clientSocket = serverSocket.accept();
            handleClientRequest(clientSocket);
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
