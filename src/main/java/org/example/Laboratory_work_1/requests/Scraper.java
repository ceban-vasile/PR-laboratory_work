package org.example.Laboratory_work_1.requests;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.Laboratory_work_1.Model.Product;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;

public class Scraper {

    private final static String QUEUE_NAME = "product_queue";

    public Scraper(){}

    public String fetchPage(String host, int port, String path) throws IOException, URISyntaxException {
        return fetchPageWithRedirect(host, port, path, 4);
    }

    private String fetchPageWithRedirect(String host, int port, String path, int redirectCount) throws IOException {
        if (redirectCount > 5) {
            throw new IOException("Too many redirects");
        }

        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.print("GET " + path + " HTTP/1.1\r\n");
            writer.print("Host: " + host + "\r\n");
            writer.print("Connection: close\r\n");
            writer.print("User-Agent: Mozilla/5.0\r\n");
            writer.print("\r\n");
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            boolean isBody = false;
            String redirectLocation = null;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    isBody = true;
                    continue;
                }

                if (isBody) {
                    response.append(line).append("\n");
                } else {
                    if (line.startsWith("HTTP/1.1 301") || line.startsWith("HTTP/1.1 302")) {
                        redirectLocation = findHeaderLocation(reader);
                    }
                }
            }

            if (redirectLocation != null) {
                URL redirectUrl = new URL(redirectLocation);
                host = redirectUrl.getHost();
                path = redirectUrl.getPath();
                return fetchPageWithRedirect(host, port, path, redirectCount + 1);
            }
            return response.toString();
        }
    }

    private static String findHeaderLocation(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Location: ")) {
                return line.split(" ")[1];  // Extract the URL from the Location header
            }
        }
        return null;
    }

    public void publishToRabbitMQ(String product) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declare a queue
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            // Publish message
            channel.basicPublish("", QUEUE_NAME, null, product.getBytes());
            System.out.println(" [x] Sent: " + product);

        } catch (Exception e) {
            System.err.println("Failed to publish message to RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
