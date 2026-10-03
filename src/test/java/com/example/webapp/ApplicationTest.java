package com.example.webapp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {

    private static Application app;
    private static HttpClient client;

    @BeforeAll
    static void startServer() throws Exception {
        app = new Application();
        app.start(8080);
        client = HttpClient.newHttpClient();
    }
    @AfterAll
    static void stopServer() {
        if (app != null) {
            app.stop();
        }
    }
    @Test
    void testIndexHtmlIsServed() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Application Foundation"));
    }
    @Test
    void testMissingResourceReturns404() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/missing-file.xyz"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
        @Test
    void testRegistrationEndpoint() throws Exception {
        // We use standard URL-encoded form data to avoid needing heavy JSON parsing dependencies
        String uniqueEmail = "apptest_" + System.currentTimeMillis() + "%40example.com"; // %40 is the @ symbol
        String formData = "name=TestApp&phone=9998887777&email=" + uniqueEmail + "&password=securepass";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/register"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // We expect the server to successfully process it and return a 201 Created status
        assertEquals(201, response.statusCode());
    }
}