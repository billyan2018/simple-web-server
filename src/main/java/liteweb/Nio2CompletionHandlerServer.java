package liteweb;

import liteweb.http.Request;
import liteweb.http.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;

public class Nio2CompletionHandlerServer extends Server {

    private static final Logger log = LogManager.getLogger(Nio2CompletionHandlerServer.class);

    public static void main(String[] args) throws IOException {

        new Nio2CompletionHandlerServer().startNio2Listen(getValidPortParam(args));
    }

    public void startNio2Listen(int port) throws IOException {

        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress("localhost", port));
            log.info("Web server listening on port {} (press CTRL-C to quit)", port);

            serverSocketChannel.accept(null, new AcceptCompletionHandler(serverSocketChannel));

            System.in.read();
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

            ByteBuffer buffer = ByteBuffer.allocate(1024 * 4);
            socketChannel.read(buffer, null, new ReadCompletionHandler(socketChannel, buffer));
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            log.error("Unexpected exception occurs when accepting.", exc);
        }
    }

    static class ReadCompletionHandler implements CompletionHandler<Integer, Void> {

        private final AsynchronousSocketChannel socketChannel;
        private final ByteBuffer buffer;

        public ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer buffer) {
            this.socketChannel = socketChannel;
            this.buffer = buffer;
        }

        @Override
        public void completed(Integer result, Void attachment) {
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)))) {
                List<String> requestContent = new ArrayList<>();
                String temp = bufferedReader.readLine();
                while (temp != null && temp.length() > 0) {
                    requestContent.add(temp);
                    temp = bufferedReader.readLine();
                }
                Request req = new Request(requestContent);
                Response res = new Response(req);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                res.write(outputStream);

                buffer.clear();
                buffer.put(outputStream.toByteArray());
                buffer.flip();
                socketChannel.write(buffer, null, new WriteCompletionHandler(socketChannel));
            } catch (IOException e) {
                log.error("IOException", e);
            }

        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            log.error("Unexpected exception occurs when reading.", exc);
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
}
