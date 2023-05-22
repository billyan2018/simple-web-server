package liteweb;

import liteweb.http.Request;
import liteweb.http.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Server {

    private static final Logger log = LogManager.getLogger(Server.class);
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException, InterruptedException {

        new Server().startListen(getValidPortParam(args));
    }


    public void startListen(int port) throws IOException, InterruptedException {

        try (ServerSocket socket = new ServerSocket(port)) {
            log.info("Web server listening on port %d (press CTRL-C to quit)", port);
            while (true) {
                TimeUnit.MILLISECONDS.sleep(1);
                handle(socket);
            }
        }
    }

    private static void handle(ServerSocket socket) {
        try (Socket clientSocket = socket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            List<String> requestContent = new ArrayList<>();
            String temp = reader.readLine();
            while(temp != null && temp.length() > 0) {
                requestContent.add(temp);
                temp = reader.readLine();
            }
            Request req = new Request(requestContent);
            Response res = new Response(req);
            res.write(clientSocket.getOutputStream());
        } catch (IOException e) {
            log.error("IO Error", e);
        }

    }

    /**
     * Parse command line arguments (string[] args) for valid port number
     *
     * @return int valid port number or default value (8080)
     */
    static int getValidPortParam(String[] args) throws NumberFormatException {
        if (args.length > 0) {
            int port = Integer.parseInt(args[0]);
            if (port > 0 && port < 65535) {
                return port;
            } else {
                throw new NumberFormatException("Invalid port! Port value is a number between 0 and 65535");
            }
        }
        return DEFAULT_PORT;
    }
}
