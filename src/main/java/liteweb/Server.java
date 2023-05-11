package liteweb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import liteweb.http.HttpRequest;
import liteweb.http.HttpResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Server {

	private static final Logger log = LogManager.getLogger(Server.class);
	private static final int DEFAULT_PORT = 8080;

	public static void main(String args[]) throws IOException, InterruptedException  {

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
			try (Socket clientSocket = socket.accept()){
				HttpRequest req = new HttpRequest(clientSocket.getInputStream());
				HttpResponse res = new HttpResponse(req);
				res.write(clientSocket.getOutputStream());
				socket.close();
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
