package liteweb.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Response {

    private static final Logger log = LogManager.getLogger(Response.class);

    public static final String VERSION = "HTTP/1.0";

    private final List<String> headers = new ArrayList<>();

    private byte[] body;

    public List<String> getHeaders() {
        return new ArrayList<>(headers);
    }

    public Response(Request req) {

        switch (req.getMethod()) {
            case HEAD:
                fillHeaders(Status._200);
                break;
            case GET:
                try {
                    String uri = req.getUri();
                    File file = new File("." + uri);
                    if (file.isDirectory()) {
                        generateResponseForFolder(uri, file);
                    } else if (file.exists()) {
                        fillHeaders(Status._200);
                        setContentType(uri);
                        fillResponse(getBytes(file));
                    } else {
                        log.info("File not found: {}}", req.getUri());
                        fillHeaders(Status._404);
                        fillResponse(Status._404.toString());
                    }
                } catch (IOException e) {
                    log.error("Response Error", e);
                    fillHeaders(Status._400);
                    fillResponse(Status._400.toString());
                }
                break;
            default:
                fillHeaders(Status._400);
                fillResponse(Status._400.toString());
        }

    }

    private void generateResponseForFolder(String uri, File file) {
        fillHeaders(Status._200);

        headers.add(ContentType.of("HTML"));
        StringBuilder result = new StringBuilder("<html><head><title>Index of ");
        result.append(uri);
        result.append("</title></head><body><h1>Index of ");
        result.append(uri);
        result.append("</h1><hr><pre>");

        File[] files = file.listFiles();
        String parentPath = getParentPath(file);
        result.append(" <a href=\"").append(parentPath).append("\">").append(parentPath).append("</a>\n");
        for (File subFile : files) {
            String subFilePath = getRealPath(subFile.getPath());
            result.append(" <a href=\"").append(subFilePath).append("\">").append(subFilePath).append("</a>\n");
        }
        result.append("<hr></pre></body></html>");
        fillResponse(result.toString());
    }

    private String getRealPath(String path) {
        if (path == null || ".".equals(path)) {
            return File.separator;
        }
        return path.substring(1);
    }

    private String getParentPath(File file) {
        if (file == null || file.getPath().equals(".")) {
            return File.separator;
        }
        return getRealPath(file.getParentFile().getPath());
    }

    private byte[] getBytes(File file) throws IOException {
        int length = (int) file.length();
        byte[] array = new byte[length];
        try (InputStream in = new FileInputStream(file)) {
            int offset = 0;
            while (offset < length) {
                int count = in.read(array, offset, (length - offset));
                offset += count;
            }
        }
        return array;
    }

    private void fillHeaders(Status status) {
        headers.add(Response.VERSION + " " + status.toString());
        headers.add("Connection: close");
        headers.add("Server: simple-web-server");
    }

    private void fillResponse(String response) {
        body = response.getBytes();
    }

    private void fillResponse(byte[] response) {
        body = response;
    }

    public void write(OutputStream outputStream) throws IOException {
        try (DataOutputStream output = new DataOutputStream(outputStream)) {
            for (String header : headers) {
                output.writeBytes(header + "\r\n");
            }
            output.writeBytes("\r\n");
            if (body != null) {
                output.write(body);
            }
            output.writeBytes("\r\n");
            output.flush();
        }
    }

    private void setContentType(String uri) {
        try {
            String ext = uri.substring(uri.indexOf(".") + 1);
            headers.add(ContentType.of(ext));
        } catch (RuntimeException e) {
            log.error("ContentType not found:", e);
        }
    }
}
