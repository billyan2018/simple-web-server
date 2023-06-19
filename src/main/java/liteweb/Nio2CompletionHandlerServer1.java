package liteweb;

import liteweb.http.Request;
import liteweb.http.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Nio2CompletionHandlerServer1 {

    private static final Logger log = LogManager.getLogger(Server.class);
    private static final int DEFAULT_PORT = 8080;
    private static final String LINE_DELIMITER = "\r\n";

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        new Nio2CompletionHandlerServer1().startListen(getValidPortParam(args));
    }

    public void startListen(int port) throws IOException, InterruptedException, ExecutionException {

        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress("localhost", port));
            log.info("Web server listening on port {} (press CTRL-C to quit)", port);
            serverSocketChannel.accept(null, new AcceptCompletionHandler(serverSocketChannel));

            System.in.read();
            log.info("Web server listening on port {} stopped.", port);
        }
    }

    static class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {

        private final AsynchronousServerSocketChannel serverSocketChannel;

        AcceptCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel) {
            this.serverSocketChannel = serverSocketChannel;
        }

        @Override
        public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
            serverSocketChannel.accept(null, this);

            try {
                Request request = readRequest(socketChannel);
                Response response = new Response(request);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                response.write(outputStream);

                ByteBuffer buffer = ByteBuffer.allocate(1024 * 4);
                buffer.put(outputStream.toByteArray());
                buffer.flip();
                socketChannel.write(buffer, null, new WriteCompletionHandler(socketChannel));
            } catch (ExecutionException | InterruptedException | IOException e) {
                log.error("Unexpected exception occurs when processing.", e);
            }
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            exc.printStackTrace();
            log.error("Unexpected exception occurs when accepting.", exc);
        }
    }

    static class WriteCompletionHandler implements CompletionHandler<Integer, Void> {

        private final AsynchronousSocketChannel socketChannel;

        WriteCompletionHandler(AsynchronousSocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void completed(Integer result, Void attachment) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                log.error("Unexpected IOException occurs when closing socket channel.", e);
            }
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            log.error("Unexpected exception occurs when writing.", exc);
        }
    }

    private static Request readRequest(AsynchronousSocketChannel socketChannel) throws ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        List<String> requestContent = new ArrayList<>();
        while (socketChannel.read(buffer).get() != -1) {
            String line = new String(buffer.array(), StandardCharsets.UTF_8);
            requestContent.addAll(Arrays.asList(line.split(LINE_DELIMITER)));
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
