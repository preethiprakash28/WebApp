package com.example.webapp;

import com.example.webapp.database.DatabaseConnection;
import com.example.webapp.database.UserRepository;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Application {

    private HttpServer server;
    // We create a single repository instance for our application to use
    private final UserRepository userRepository = new UserRepository(new DatabaseConnection());

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // ---------------------------------------------------------
        // NEW ENDPOINT: Registration API
        // ---------------------------------------------------------
        server.createContext("/api/register", exchange -> {
            // Only allow POST requests for registration
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
                return;
            }
            try {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String name = null, phone = null, email = null, password = null;
                String[] pairs = body.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2) {
                        String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                        switch (key) {
                            case "name": name = value; break;
                            case "phone": phone = value; break;
                            case "email": email = value; break;
                            case "password": password = value; break;
                        }
                    }
                }
                boolean success = userRepository.saveUser(name, phone, email, password);
                
                if (success) {
                    String response = "Registration successful";
                    exchange.sendResponseHeaders(201, response.getBytes().length); // 201 Created
                    try (OutputStream os = exchange.getResponseBody()) { os.write(response.getBytes()); }
                } else {
                    String response = "Registration failed: Invalid input or email already exists.";
                    exchange.sendResponseHeaders(400, response.getBytes().length); // 400 Bad Request
                    try (OutputStream os = exchange.getResponseBody()) { os.write(response.getBytes()); }
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1); // 500 Internal Server Error
            }
        });
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            try (InputStream is = Application.class.getResourceAsStream("/static" + path)) {
                if (is == null) {
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) { os.write(response.getBytes()); }
                    return;
                }
                if (path.endsWith(".html")) exchange.getResponseHeaders().set("Content-Type", "text/html");
                else if (path.endsWith(".css")) exchange.getResponseHeaders().set("Content-Type", "text/css");
                else if (path.endsWith(".js")) exchange.getResponseHeaders().set("Content-Type", "application/javascript");
                byte[] fileBytes = is.readAllBytes();
                exchange.sendResponseHeaders(200, fileBytes.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(fileBytes); }    
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
            }
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server stopped.");
        }
    }
    public static void main(String[] args) throws IOException {
        Application app = new Application();
        app.start(8080);
    }
}