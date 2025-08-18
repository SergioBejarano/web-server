package co.edu.escuelaing.httpserver;

import java.net.URI;

/**
 * RestServices provides static methods to handle specific RESTful services
 * such as greeting and echoing messages.
 * 
 * These services are used by the HttpServer to respond to GET and POST
 * requests.
 * 
 * @author sergio.bejarano-r
 */
public class RestServices {

    /**
     * Provides a basic JSON-based hello service for GET requests.
     * <p>
     * If a "name" parameter is present in the query string, it is used in the
     * response; otherwise, it defaults to "Mundo".
     * </p>
     *
     * @param requesturi The URI of the request containing optional query
     *                   parameters.
     * @return An HTTP 200 OK response with a JSON message.
     */
    public static String helloService(URI requesturi) {
        String name = "Mundo";
        String query = requesturi.getQuery();
        if (query != null && query.contains("=")) {
            name = query.split("=")[1];
        }
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Connection: close\r\n\r\n" +
                "{\"mensaje\": \"Hola " + name + "\"}";
    }

    /**
     * Provides a basic JSON-based echo service for POST requests.
     * <p>
     * Returns the message sent in the body as a JSON object with key "echo".
     * </p>
     *
     * @param body The body of the POST request.
     * @return An HTTP 200 OK response with a JSON echo message.
     */
    public static String echoService(String body) {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Connection: close\r\n\r\n" +
                "{\"echo\": \"" + body.replace("\"", "\\\"") + "\"}";
    }
}
