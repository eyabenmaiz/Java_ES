package ClientFX;

import java.io.*;
import java.net.*;
import java.util.*;

public class ApiClient {

    private static final String BASE = "http://localhost:7778";

    public List<CameraModel> getAllCameras() throws IOException {
        return parseList(sendGet("/api/cameras"));
    }

    public CameraModel startCamera(int id) throws IOException {
        return parseOne(sendPost(
            "/api/cameras/" + id + "/record",
            "{\"action\":\"START\"}"
        ));
    }

    public CameraModel stopCamera(int id) throws IOException {
        return parseOne(sendPost(
            "/api/cameras/" + id + "/record",
            "{\"action\":\"STOP\"}"
        ));
    }

    public List<CameraModel> commandAll(String action) throws IOException {
        return parseList(sendPost(
            "/api/cameras/all/record",
            "{\"action\":\"" + action + "\"}"
        ));
    }

    private String sendGet(String endpoint) throws IOException {
        HttpURLConnection c = open(endpoint, "GET");
        c.connect();
        return read(c.getInputStream());
    }

    private String sendPost(String endpoint, String body) throws IOException {
        HttpURLConnection c = open(endpoint, "POST");
        c.setDoOutput(true);
        byte[] bytes = body.getBytes("UTF-8");
        c.setRequestProperty("Content-Length", "" + bytes.length);
        try (OutputStream os = c.getOutputStream()) {
            os.write(bytes);
            os.flush();
        }
        int code = c.getResponseCode();
        System.out.println("[HTTP] POST " + endpoint + " → " + code);
        InputStream is = (code >= 200 && code < 300)
                         ? c.getInputStream()
                         : c.getErrorStream();
        return read(is);
    }

    private HttpURLConnection open(String endpoint,
                                   String method) throws IOException {
        URL url = new URL(BASE + endpoint);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod(method);
        c.setRequestProperty("Content-Type",  "application/json");
        c.setRequestProperty("Accept",        "application/json");
        c.setRequestProperty("Connection",    "close");
        c.setConnectTimeout(5000);
        c.setReadTimeout(5000);
        return c;
    }

    private String read(InputStream is) throws IOException {
        if (is == null) return "";
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] chunk = new byte[1024];
        int n;
        while ((n = is.read(chunk)) != -1) buf.write(chunk, 0, n);
        return buf.toString("UTF-8");
    }

    private CameraModel parseOne(String json) {
        return new CameraModel(
            Integer.parseInt(extractField(json, "cameraId")),
            extractField(json, "status"),
            extractField(json, "lastUpdate")
        );
    }

    private List<CameraModel> parseList(String json) {
        List<CameraModel> list = new ArrayList<>();
        int start = 0;
        while (true) {
            int open  = json.indexOf('{', start);
            int close = json.indexOf('}', open);
            if (open < 0 || close < 0) break;
            list.add(parseOne(json.substring(open, close + 1)));
            start = close + 1;
        }
        return list;
    }

    private String extractField(String json, String key) {
        String p = "\"" + key + "\"";
        int i = json.indexOf(p);
        if (i < 0) return "";
        int c = json.indexOf(':', i + p.length());
        int s = c + 1;
        while (s < json.length() &&
               Character.isWhitespace(json.charAt(s))) s++;
        if (json.charAt(s) == '"') {
            int e = json.indexOf('"', s + 1);
            return e < 0 ? "" : json.substring(s + 1, e);
        }
        int e = s;
        while (e < json.length() &&
               ",}]".indexOf(json.charAt(e)) < 0) e++;
        return json.substring(s, e).trim();
    }
}