package liteweb.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ContentType enum uses the file extension to loosely map the available content type based on common media types:
 * http://en.wikipedia.org/wiki/Internet_media_type
 */
final class ContentType {
	private static final Map<String, String> EXTENSION_MAP;

	static {
		Map<String, String> extensions = new HashMap<>();
		extensions.put("CSS", "Content-Type: text/css");
		extensions.put("GIF", "Content-Type: image/gif");
		extensions.put("HTM","Content-Type: text/html");
		extensions.put("HTML", "Content-Type: text/html");
		extensions.put("ICO", "Content-Type: image/x-icon");
		extensions.put("JPG","Content-Type: image/jpeg");
		extensions.put("JPEG", "Content-Type: image/jpeg");
		extensions.put("PNG", "Content-Type: image/png");
		extensions.put("XML", "Content-type: text/xml");

		EXTENSION_MAP = Collections.unmodifiableMap(new HashMap<>());
	}
	private ContentType() {

	}
	static String of(String extension) {
		String defined = EXTENSION_MAP.get(extension.toUpperCase());
		if (defined != null) {
			return defined;
		} else {
			return "Content-Type: text/html";
		}
	}
}
