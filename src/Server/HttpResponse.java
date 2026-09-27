package Server;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    public static final int OK             = 200;
    public static final int BAD_REQUEST    = 400;
    public static final int NOT_FOUND      = 404;
    public static final int INTERNAL_ERROR = 500;

    private static final String CRLF = "\r\n";
    private final OutputStream out;

    public HttpResponse(OutputStream out) { this.out = out; }

    public void sendJson(int code, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        // On écrit tout dans un seul buffer pour éviter les problèmes de flush
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        String headers =
            "HTTP/1.1 " + code + " " + reason(code) + CRLF +
            "Content-Type: application/json; charset=UTF-8" + CRLF +
            "Content-Length: " + body.length + CRLF +
            "Access-Control-Allow-Origin: *" + CRLF +
            "Connection: close" + CRLF +
            CRLF;

        buffer.write(headers.getBytes(StandardCharsets.UTF_8));
        buffer.write(body);

        // Un seul write → pas de problème de timing
        out.write(buffer.toByteArray());
        out.flush();
    }

    public void sendError(int code, String msg) throws IOException {
        sendJson(code, "{\"error\":\"" + msg + "\"}");
    }

    private String reason(int code) {
        switch (code) {
            case 200: return "OK";
            case 400: return "Bad Request";
            case 404: return "Not Found";
            case 500: return "Internal Server Error";
            default:  return "Unknown";
        }
    }
}