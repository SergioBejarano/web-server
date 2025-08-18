package co.edu.escuelaing.httpserver;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class HttpServerTest {

    private static ExecutorService executor;

    @BeforeAll
    static void startServer() {
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                HttpServer.main(new String[] {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Espera breve para que arranque
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }

    @AfterAll
    static void stopServer() {
        executor.shutdownNow();
    }

    @Test
    void shouldReturnHelloWorldOnGet() throws Exception {
        URL url = new URL("http://localhost:35000/app/hello?name=Prueba");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        assertEquals(200, conn.getResponseCode());

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = reader.readLine();
        assertTrue(response.contains("Hola Prueba"));
    }

    @Test
    void shouldReturnNotFoundOnInvalidPath() throws Exception {
        URL url = new URL("http://localhost:35000/invalidpath");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        assertEquals(404, conn.getResponseCode());
    }

    @Test
    void shouldEchoOnPost() throws Exception {
        URL url = new URL("http://localhost:35000/app/hello");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        String message = "Hola Mundo";
        byte[] body = message.getBytes();
        conn.setFixedLengthStreamingMode(body.length);
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.connect();

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body);
        }

        assertEquals(200, conn.getResponseCode());

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = reader.readLine();
        assertTrue(response.contains("Hola Mundo"));
    }

    @Test
    void shouldSend404WhenFileDoesNotExist() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        URI uri = new URI("/archivoInexistente.html");
        var method = HttpServer.class.getDeclaredMethod("serveStaticFile", java.io.OutputStream.class, URI.class);
        method.setAccessible(true);
        method.invoke(null, out, uri);
        String response = out.toString();
        assertTrue(response.contains("HTTP/1.1 404 Not Found"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("<h1>404 Not Found</h1>"));
    }

}
