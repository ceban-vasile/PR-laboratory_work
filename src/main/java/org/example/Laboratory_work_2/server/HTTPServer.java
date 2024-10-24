package org.example.Laboratory_work_2.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HTTPServer {

    public void runServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

       // server.createContext("/signup", new SignUpHandler());
       // server.createContext("/login", new LogInHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server is running on port 8000...");
    }
}
