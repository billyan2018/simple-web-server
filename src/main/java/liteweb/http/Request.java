package liteweb.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * Request class parses the HTTP Request Line (method, URI, version)
 * and Headers http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
 */
public class Request {

	private static final Logger logger = LogManager.getLogger(Request.class);

    private final List<String> headers = new ArrayList<>();

    private Method method;

    private String uri;

    private String version;

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public List<String> getHeaders() {
        return new ArrayList<>(headers);
    }

    public Request(List<String> requests) {
        parseRequestLine(requests.get(0));
        for (int i = 1; i < requests.size(); i++) {
            addToHeader(requests.get(i));
        }
    }


    private void parseRequestLine(String str) {
		logger.info("request line:", str);
        String[] split = str.split("\\s+");
        try {
            method = Method.valueOf(split[0]);
        } catch (RuntimeException e) {
            method = Method.UNRECOGNIZED;
        }
        uri = split[1];
        version = split[2];
    }

    private void addToHeader(String str) {
        headers.add(str);
    }
}
