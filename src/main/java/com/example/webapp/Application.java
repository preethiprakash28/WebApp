package com.example.webapp;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Application {

    private HttpServer server;

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // This handler will intercept ALL requests
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            
            // Map the root route directly to our index.html
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            // Look for the file inside the src/main/resources/static/ folder
            try (InputStream is = Application.class.getResourceAsStream("/static" + path)) {
                
                if (is == null) {
                    // File not found, handle gracefully with a 404
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }

                // Set the correct Content-Type so the browser knows how to render it
                if (path.endsWith(".html")) {
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                } else if (path.endsWith(".css")) {
                    exchange.getResponseHeaders().set("Content-Type", "text/css");
                } else if (path.endsWith(".js")) {
                    exchange.getResponseHeaders().set("Content-Type", "application/javascript");
                }
                
                // Read the file and send a 200 OK success status
                byte[] fileBytes = is.readAllBytes();
                exchange.sendResponseHeaders(200, fileBytes.length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileBytes);
                }
                
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