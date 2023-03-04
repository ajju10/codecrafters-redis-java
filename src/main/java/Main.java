import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");
        ServerSocket serverSocket;
        int port = 6379;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            //Wait for connection from client
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        doResponse(clientSocket);
                    } catch (IOException E) {
                        System.out.print("IOException" + E.getMessage());
                    } finally {
                        try {
                            if (clientSocket != null) {
                                clientSocket.close();
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("IOException" + e.getMessage());
        }
    }

    private static void doResponse(Socket clientSocket) throws IOException {
        if (clientSocket.isConnected()) {
            byte[] buffer = new byte[1024];
            while (clientSocket.getInputStream().read(buffer) != -1) {
                String consult;
                String msg;
                msg = new String(buffer);
                if (msg.startsWith("*")) {
                    consult = handleBulkCommand(msg);
                } else {
                    consult = handlePing(msg);
                }
                clientSocket.getOutputStream().write((consult + "\r\n").getBytes());
                clientSocket.getOutputStream().flush();
                buffer = new byte[1024];
            }
        }
    }

    private static String handleBulkCommand(String receivedMessage) {
        List<String> list = parseBulkMessageArguments(receivedMessage);
        return handleCommand(list);
    }

    private static String handleCommand(List<String> list) {
        String command = list.get(0);
        if ("ECHO".equalsIgnoreCase(command)) {
            return handleEcho(list);
        } else if ("PING".equalsIgnoreCase(command)) {
            return handlePing(command);
        } else {
            return handleUnknownMessage(command);
        }
    }

    private static String handleUnknownMessage(String command) {
        String msg = "Unknown command" + command;
        System.out.println("[" + Thread.currentThread() + "]" + msg);
        return msg;
    }

    private static String handlePing(String command) {
        System.out.println("[" + Thread.currentThread() + "] Return + PONG");
        return "+PONG";
    }

    private static String handleEcho(List<String> list) {
        String res = list.get(1);
        System.out.println("[" + Thread.currentThread() + "] Return" + res);
        return "+" + res;
    }

    private static List<String> parseBulkMessageArguments(String receivedMessage) {
        List<String> args = Arrays.stream(receivedMessage.split("\r\n"))
                .filter(arg -> !arg.trim().isEmpty() &&
                        !arg.startsWith("*") &&
                        !arg.startsWith("$")
                ).collect(Collectors.toList());
        System.out.println("[" + Thread.currentThread() + "]Received bulk args :" + args);
        return args;
    }
}
