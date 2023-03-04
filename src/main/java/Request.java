import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Request {
    private final Socket socket;
    private String req;
    private List<String> arr;

    public Request(Socket socket) {
        this.socket = socket;
    }

    public String parse() throws IOException {
        String res;
        byte[] buf = new byte[1024];
        socket.getInputStream().read(buf);
        req = new String(buf);
        arr = req.lines()
                .filter(line -> !line.isEmpty() && !line.isBlank() && !line.contains("$") && !line.contains("*"))
                .toList();
        if (req.startsWith("$")) {
            res = respondPing();
        } else {
            res = respondBulkString();
        }
        return res;
    }

    private String respondBulkString() {
        if (arr.get(0).equalsIgnoreCase("echo")) {
            return respondEcho();
        }
        return respondPing();
    }

    private String respondEcho() {
        return "+" + arr.get(1) + "\r\n";
    }

    private String respondPing() {
        return "+PONG\r\n";
    }
}
