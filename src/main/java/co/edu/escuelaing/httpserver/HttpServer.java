package co.edu.escuelaing.httpserver;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple web server implementation that listens on a specific port and
 * serves static files from a predefined directory or responds to specific
 * API requests.
 * 
 * Supports:
 * - GET /app/hello -> returns a JSON greeting.
 * - POST /app/hello -> echoes back the JSON message received in the body.
 * - Static file serving from resources directory.
 * 
 * @author sergio.bejarano-r
 */
public class HttpServer {

    private static final String WWW_DIR = "src/main/java/co/edu/escuelaing/httpserver/resources";
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
    }

    /**
     * Entry point of the HTTP server.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException if an I/O error occurs when opening the socket.
     */
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(35000)) {
            System.out.println("Server listening on port 35000...");
            while (true) {
                try (
                        Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        OutputStream out = clientSocket.getOutputStream()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    handleClientRequest(in, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handles an individual client request, parsing the HTTP method,
     * URI, headers, and body if needed.
     *
     * @param in  Input stream from the client.
     * @param out Output stream to send response.
     * @throws Exception if an error occurs while processing the request.
     */
    private static void handleClientRequest(BufferedReader in, OutputStream out) throws Exception {
        String method = "";
        URI requestUri = null;
        int contentLength = 0;
        boolean isFirstLine = true;
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (isFirstLine) {
                String[] requestParts = inputLine.split(" ");
                if (requestParts.length >= 2) {
                    method = requestParts[0];
                    requestUri = new URI(requestParts[1]);
                    System.out.println("Method: " + method + " | Path: " + requestUri.getPath());
                }
                isFirstLine = false;
            } else if (inputLine.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(inputLine.split(":")[1].trim());
            }
            if (inputLine.isEmpty())
                break;
        }
        if ("GET".equalsIgnoreCase(method)) {
            handleGet(out, requestUri);
        } else if ("POST".equalsIgnoreCase(method)) {
            handlePost(in, out, requestUri, contentLength);
        } else {
            send404(out);
        }
    }

    /**
     * Handles a GET request.
     *
     * @param out        Output stream to client.
     * @param requestUri The URI requested.
     * @throws IOException if an I/O error occurs.
     */
    private static void handleGet(OutputStream out, URI requestUri) throws IOException {
        if (requestUri != null && requestUri.getPath().startsWith("/app/hello")) {
            out.write(RestServices.helloService(requestUri).getBytes());
            out.flush();
        } else {
            serveStaticFile(out, requestUri);
        }
    }

    /**
     * Handles a POST request.
     *
     * @param in            Input stream from client.
     * @param out           Output stream to client.
     * @param requestUri    The URI requested.
     * @param contentLength Content length of the body.
     * @throws IOException if an I/O error occurs.
     */
    private static void handlePost(BufferedReader in, OutputStream out, URI requestUri, int contentLength)
            throws IOException {
        String body = readRequestBody(in, contentLength);

        if (requestUri != null && requestUri.getPath().startsWith("/app/hello")) {
            out.write(RestServices.echoService(body).getBytes());
            out.flush();
        } else {
            send404(out);
        }
    }

    /**
     * Reads the request body based on the Content-Length header.
     *
     * @param in            Input stream from client.
     * @param contentLength Number of characters to read.
     * @return Request body as String.
     * @throws IOException if an I/O error occurs.
     */
    private static String readRequestBody(BufferedReader in, int contentLength) throws IOException {
        if (contentLength <= 0) {
            return "";
        }
        char[] bodyChars = new char[contentLength];
        int read = 0;
        while (read < contentLength) {
            int r = in.read(bodyChars, read, contentLength - read);
            if (r == -1)
                break;
            read += r;
        }
        return new String(bodyChars);
    }

    /**
     * Serves a static file from the predefined WWW directory.
     *
     * @param out        Output stream to client.
     * @param requestUri Requested URI.
     * @throws IOException if an I/O error occurs.
     */
    private static void serveStaticFile(OutputStream out, URI requestUri) throws IOException {
        String path = requestUri.getPath();
        if (path.equals("/")) {
            path = "/index.html";
        }

        if (path.contains("..")) {
            send404(out);
            return;
        }

        File file = new File(WWW_DIR + path);
        String ext = getExtension(file.getName());

        if (!MIME_TYPES.containsKey(ext) || !file.exists() || file.isDirectory()) {
            send404(out);
            return;
        }

        String contentType = MIME_TYPES.get(ext);
        byte[] content = Files.readAllBytes(file.toPath());

        String header = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n\r\n";

        out.write(header.getBytes());
        out.write(content);
        out.flush();
    }

    /**
     * Sends a basic HTTP 404 Not Found response.
     *
     * @param out Output stream to client.
     * @throws IOException if an I/O error occurs.
     */
    private static void send404(OutputStream out) throws IOException {
        String msg = "<h1>404 Not Found</h1>";
        String header = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + msg.length() + "\r\n" +
                "Connection: close\r\n\r\n";
        out.write(header.getBytes());
        out.write(msg.getBytes());
        out.flush();
    }

    /**
     * Extracts the file extension from a file name.
     *
     * @param fileName File name.
     * @return Lowercase file extension or empty string if not found.
     */
    private static String getExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        return (idx > 0 && idx < fileName.length() - 1) ? fileName.substring(idx + 1).toLowerCase() : "";
    }

}