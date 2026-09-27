package Server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(String method, String path,
                        Map<String, String> headers, String body) {
        this.method  = method;
        this.path    = path;
        this.headers = headers;
        this.body    = body;
    }

    public static HttpRequest parse(InputStream rawIn) throws IOException {

        // ── 1. Lit octet par octet jusqu'à \r\n\r\n (fin des headers) ──
        ByteArrayOutputStream headerBuf = new ByteArrayOutputStream();
        int prev3 = 0, prev2 = 0, prev1 = 0, cur;
        while ((cur = rawIn.read()) != -1) {
            headerBuf.write(cur);
            // Détecte la séquence \r\n\r\n
            if (prev3 == '\r' && prev2 == '\n' &&
                prev1 == '\r' && cur  == '\n') {
                break;
            }
            prev3 = prev2; prev2 = prev1; prev1 = cur;
        }

        String headerSection = headerBuf.toString("UTF-8");
        if (headerSection.trim().isEmpty()) return null;

        String[] lines = headerSection.split("\r\n");
        if (lines.length == 0) return null;

        // ── 2. Ligne de requête ──
        String[] parts = lines[0].split(" ");
        if (parts.length < 2) return null;
        String method = parts[0].toUpperCase();
        String path   = parts[1];

        // ── 3. Headers ──
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            int colon = lines[i].indexOf(':');
            if (colon > 0) {
                String key   = lines[i].substring(0, colon).trim().toLowerCase();
                String value = lines[i].substring(colon + 1).trim();
                headers.put(key, value);
            }
        }

        // ── 4. Body (lit exactement Content-Length octets) ──
        String body = "";
        String cl = headers.get("content-length");
        if (cl != null) {
            int len = Integer.parseInt(cl.trim());
            if (len > 0) {
                byte[] bodyBytes = new byte[len];
                int total = 0;
                while (total < len) {
                    int read = rawIn.read(bodyBytes, total, len - total);
                    if (read < 0) break;
                    total += read;
                }
                body = new String(bodyBytes, 0, total, "UTF-8");
            }
        }

        System.out.println("[PARSE] " + method + " " + path);
        System.out.println("[PARSE] Body : " + body);

        return new HttpRequest(method, path, headers, body);
    }

    public String getMethod() { return method; }
    public String getPath()   { return path; }
    public String getBody()   { return body; }

    public String getJsonField(String key) {
        if (body == null || body.isBlank()) return null;
        String pattern = "\"" + key + "\"";
        int idx = body.indexOf(pattern);
        if (idx < 0) return null;
        int colon = body.indexOf(':', idx + pattern.length());
        if (colon < 0) return null;
        int start = colon + 1;
        while (start < body.length() &&
               Character.isWhitespace(body.charAt(start))) start++;
        if (start >= body.length()) return null;
        if (body.charAt(start) == '"') {
            int end = body.indexOf('"', start + 1);
            return end < 0 ? null : body.substring(start + 1, end);
        }
        int end = start;
        while (end < body.length() &&
               ",}".indexOf(body.charAt(end)) < 0) end++;
        return body.substring(start, end).trim();
    }

    @Override
    public String toString() { return method + " " + path; }
}