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
     * Handles GET and POST requests. GET requests to /app/hello return a JSON
     * greeting.
     * POST requests to /app/hello return a JSON echo of the message sent in the
     * body.
     * Other requests serve static files or return 404 if not found.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException if an I/O error occurs when opening the socket.
     */
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(35000)) {
            System.out.println("Servidor escuchando en puerto 35000...");
            while (true) {
                try (
                        Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        OutputStream out = clientSocket.getOutputStream()) {
                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                    String inputLine;
                    boolean isFirstLine = true;
                    URI requestUri = null;
                    String method = "";
                    int contentLength = 0;
                    while ((inputLine = in.readLine()) != null) {
                        if (isFirstLine) {
                            String[] requestParts = inputLine.split(" ");
                            if (requestParts.length >= 2) {
                                method = requestParts[0];
                                requestUri = new URI(requestParts[1]);
                                System.out.println("Método: " + method + " | Path: " + requestUri.getPath());
                            }
                            isFirstLine = false;
                        } else if (inputLine.startsWith("Content-Length:")) {
                            contentLength = Integer.parseInt(inputLine.split(":")[1].trim());
                        }
                        if (inputLine.isEmpty())
                            break;
                    }
                    if ("GET".equalsIgnoreCase(method)) {
                        if (requestUri != null && requestUri.getPath().startsWith("/app/hello")) {
                            String response = RestServices.helloService(requestUri);
                            out.write(response.getBytes());
                            out.flush();
                        } else {
                            serveStaticFile(out, requestUri);
                        }
                    } else if ("POST".equalsIgnoreCase(method)) {
                        String body = "";
                        if (contentLength > 0) {
                            char[] bodyChars = new char[contentLength];
                            int read = 0;
                            while (read < contentLength) {
                                int r = in.read(bodyChars, read, contentLength - read);
                                if (r == -1)
                                    break;
                                read += r;
                            }
                            body = new String(bodyChars);
                        }
                        if (requestUri != null && requestUri.getPath().startsWith("/app/hello")) {
                            String response = RestServices.echoService(body);
                            out.write(response.getBytes());
                            out.flush();
                        } else {
                            send404(out);
                        }
                    } else {
                        send404(out);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Serves a static file from the predefined WWW directory.
     * <p>
     * If the path is empty, it defaults to "index.html". This method verifies
     * the file exists, matches a supported MIME type, and is not a directory.
     * Directory traversal attempts are blocked.
     * </p>
     *
     * @param out        The output stream to write the HTTP response.
     * @param requestUri The URI of the requested resource.
     * @throws IOException if an I/O error occurs while reading or writing the file.
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
     * Sends a basic HTTP 404 Not Found response to the client.
     *
     * @param out The output stream to write the HTTP response.
     * @throws IOException if an I/O error occurs while writing the response.
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
     * Extracts the file extension from the given file name.
     *
     * @param fileName The file name to process.
     * @return The lowercase file extension, or an empty string if none is found.
     */
    private static String getExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        return (idx > 0 && idx < fileName.length() - 1) ? fileName.substring(idx + 1).toLowerCase() : "";
    }

}