package liteweb;

import liteweb.http.Request;
import liteweb.http.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

public class Nio2Server {

    private static final Logger log = LogManager.getLogger(Server.class);
    private static final int DEFAULT_PORT = 8080;
    private static final BlockingQueue<Socket> SOCKET_BLOCKING_QUEUE = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        new Nio2Server().startListen(getValidPortParam(args));
    }

    public void startListen(int port) throws IOException, InterruptedException, ExecutionException {

        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress("localhost", port));
            serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
                    serverSocketChannel.accept(null, this);

                    try {
                        Request request = readRequest(socketChannel);
                        Response response = new Response(request);


                    } catch (ExecutionException | InterruptedException e) {
                        log.error("Unexpected exception occurs when processing.", e);
                    } finally {
                        try {
                            socketChannel.close();
                        } catch (IOException e) {
                            log.error("Unexpected IOException occurs when closing.", e);
                        }
                    }

                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    exc.printStackTrace();
                    log.error("Unexpected exception occurs.", exc);
                }
            });

            log.info("Web server listening on port {} (press CTRL-C to quit)", port);
        }
    }

    private static Request readRequest(AsynchronousSocketChannel socketChannel) throws ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(10240);
        buffer.flip();

        List<String> requestContent = new ArrayList<>();
        while (socketChannel.read(buffer).get() < 0) {
            String line = new String(buffer.array());
            requestContent.addAll(Arrays.asList(line.split("\r\n")));
            buffer.flip();
        }
        return new Request(requestContent);
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
