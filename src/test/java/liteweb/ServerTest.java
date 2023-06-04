package liteweb;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @ParameterizedTest
    @CsvSource({", 8080", "1234, 1234", "8080, 8080"})
    void shouldReturnTrue_whenValid(String value, int port) {
        String[] args =  value == null ? new String[]{} : new String[]{value};
        assertEquals(port, Server.getValidPortParam(args));
    }

    @ParameterizedTest
    @CsvSource({"asda", "0", "65535"})
    void wrongParamThrowException(String value) {
        String[] args = {value};
        ;
        assertThrows(NumberFormatException.class, () -> {
            Server.getValidPortParam(args);
        });
    }

    @Test
    void shouldReturn200_whenRequested() throws IOException, InterruptedException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Socket clientSocket = new Socket() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(
                        "HEAD / HTTP/1.1\n\n".getBytes()
                );
            }

            @Override
            public OutputStream getOutputStream() {
                return output;
            }
        };

        ServerSocket serverSocket = new ServerSocket() {
            @Override
            public Socket accept() {
                return clientSocket;
            }
        };

        Server.accept(serverSocket);

        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(Server::consume);
        exec.shutdown();
        exec.awaitTermination(5, TimeUnit.SECONDS);

        assertTrue(output.toString(StandardCharsets.UTF_8.name()).contains("HTTP/1.0 200 OK"));
        }
    }

