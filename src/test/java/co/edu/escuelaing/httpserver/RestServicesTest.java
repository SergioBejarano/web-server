package co.edu.escuelaing.httpserver;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.Test;

public class RestServicesTest {

    @Test
    void helloService_shouldReturnDefaultGreeting() throws Exception {
        URI uri = new URI("http://localhost:35000/hello");
        String response = RestServices.helloService(uri);

        assertTrue(response.startsWith("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: application/json"));
        assertTrue(response.contains("{\"mensaje\": \"Hola Mundo\"}"));
        assertFalse(response.contains("Hola Sergio"));
    }

    @Test
    void helloService_shouldReturnGreetingWithName() throws Exception {
        URI uri = new URI("http://localhost:35000/hello?name=Sergio");
        String response = RestServices.helloService(uri);

        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Hola Sergio"));
        assertFalse(response.contains("Hola Mundo"));
    }

    @Test
    void helloService_notShouldUseWrongKey() throws Exception {
        URI uri = new URI("http://localhost:35000/hello?user=Sergio");
        String response = RestServices.helloService(uri);

        assertFalse(response.contains("Hola Mundo"));
        assertTrue(response.contains("Hola Sergio"));
    }

    @Test
    void echoService_shouldEchoBasicMessage() {
        String response = RestServices.echoService("Hola mundo");

        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Content-Type: application/json"));
        assertTrue(response.contains("{\"echo\": \"Hola mundo\"}"));
    }

    @Test
    void echoService_shouldEscapeQuotes() {
        String response = RestServices.echoService("Hola \"Mundo\"");

        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("{\"echo\": \"Hola \\\"Mundo\\\"\"}"));
        assertFalse(response.contains("Hola \"Mundo\""));
    }

    @Test
    void echoService_notShouldFailWithEmptyBody() {
        String response = RestServices.echoService("");

        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("{\"echo\": \"\"}"));
    }
}
