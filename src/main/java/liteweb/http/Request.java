package liteweb.http;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Request class parses the HTTP Request Line (method, URI, version)
 * and Headers http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
 */
public class Request {

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

	public Request(InputStream is) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			String contentLine = reader.readLine();
			parseRequestLine(contentLine);

			while (!"".equals(contentLine)) {
				contentLine = reader.readLine();
				addToHeader(contentLine);
			}
		}
	}

	private void parseRequestLine(String str) {

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
