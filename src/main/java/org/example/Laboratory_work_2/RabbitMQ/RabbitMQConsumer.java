package org.example.Laboratory_work_2.RabbitMQ;

import com.rabbitmq.client.*;
import org.apache.hc.client5.http.fluent.Request;

import java.nio.charset.StandardCharsets;

public class RabbitMQConsumer {

    private static final String QUEUE_NAME = "product_queue";
    private static final String LAB2_URL = "http://localhost:8000/addProduct";

    public void startConsumer() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");


        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received: " + message);
                // Forward message to LAB2 web server
                try {
                    // Forward JSON to the web server
                    Request.post(LAB2_URL)
                            .bodyString(message, org.apache.hc.core5.http.ContentType.APPLICATION_JSON)
                            .execute()
                            .returnContent();
                    System.out.println("Forwarded to LAB2: " + message);

                } catch (Exception e) {
                    System.err.println("Failed to process message: " + e.getMessage());
                }
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });

            System.out.println("Consumer started. Press CTRL+C to exit...");
            while (true) { }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
