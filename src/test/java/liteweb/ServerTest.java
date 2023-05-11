package liteweb;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServerTest {

    @ParameterizedTest
    @CsvSource({"1234, 1234", "8080, 8080"})
    void shouldReturnTrue_whenValid(String value, int port) {
        String[] args = {value};
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
}
