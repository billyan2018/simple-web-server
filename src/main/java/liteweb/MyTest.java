package liteweb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class MyTest {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String RESPONSE_TEMPLATE = "HTTP/1.1 200 OK\r\nContent-Length: %d\r\n\r\n%s";
    private static final String RESPONSE_BODY = "<html><body><h1>Hello, World!</h1></body></html>";

    public static void main(String[] args) throws IOException {
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.socket().bind(new InetSocketAddress(8081), 60);
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Web Server started");

        while (true) {
            // 轮询Selector上的事件
            selector.select();
            System.out.println("select");

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();
                if (selectionKey.isAcceptable()) {
                    System.out.println("acceptable");
                    ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel acceptedChannel = channel.accept();
                    acceptedChannel.configureBlocking(false);
                    acceptedChannel.register(selector, SelectionKey.OP_READ);
                }
                if (selectionKey.isReadable()) {
                    System.out.println("isReadable");
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    // 读取请求数据
                    int readBytes = 0;
                    while ((readBytes = channel.read(byteBuffer)) > 0 || byteBuffer.position() == 0) {
                        // 判断 ByteBuffer 容量是否足够
                        if (!byteBuffer.hasRemaining()) {
                            // 扩容 ByteBuffer
                            ByteBuffer newBuffer = ByteBuffer.allocate(byteBuffer.capacity() * 2);
                            byteBuffer.flip();
                            newBuffer.put(byteBuffer);
                            byteBuffer = newBuffer;
                        }
                    }
                    if (readBytes == -1) {
                        channel.close();
                        selectionKey.cancel();
                        return;
                    }
                    byteBuffer.flip();
                    byte[] requestData = new byte[byteBuffer.remaining()];
                    byteBuffer.get(requestData);
                    String request = new String(requestData,CHARSET).trim();
                    System.out.println("request=" + request);
                    String responseText = String.format(RESPONSE_TEMPLATE, RESPONSE_BODY.length(), RESPONSE_BODY);
                    ByteBuffer response = ByteBuffer.wrap(responseText.getBytes(CHARSET));
                    channel.write(response);
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                }
                if (selectionKey.isWritable()) {
                    System.out.println("isWritable");
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    buffer.flip();
                    channel.write(buffer);
                    System.out.println(new String(buffer.array(), CHARSET));
                    selectionKey.interestOps(SelectionKey.OP_READ);
                    channel.close();
                }
                selectionKeyIterator.remove();
            }
        }
    }
}
