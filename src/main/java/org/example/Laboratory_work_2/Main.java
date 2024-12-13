package org.example.Laboratory_work_2;

import org.example.Laboratory_work_2.RabbitMQ.RabbitMQConsumer;
import org.example.Laboratory_work_2.chat_room.ChatWebSocketServer;
import org.example.Laboratory_work_2.server.HTTPServer;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws Exception {

        new Thread(() -> {
            ChatWebSocketServer webSocketServer = new ChatWebSocketServer();
            webSocketServer.start();
        }).start();

        new Thread(() -> {
            try {
                HTTPServer httpServer = new HTTPServer();
                httpServer.runServer();
                new RabbitMQConsumer().startConsumer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}