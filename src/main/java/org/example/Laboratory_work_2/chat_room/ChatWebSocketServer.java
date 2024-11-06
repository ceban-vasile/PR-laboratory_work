package org.example.Laboratory_work_2.chat_room;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketServer extends WebSocketServer {

    private static final int PORT = 8001;
    private List<WebSocket> clients = new CopyOnWriteArrayList<>();
    private Map<WebSocket, String> userNames = new ConcurrentHashMap<>();
    private List<String> messageHistory = new CopyOnWriteArrayList<>();

    public ChatWebSocketServer() {
        super(new InetSocketAddress(PORT));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        conn.send("Welcome to the chat room!");

        for (String message : messageHistory) {
            conn.send(message);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String userName = userNames.getOrDefault(conn, "A user");
        clients.remove(conn);
        userNames.remove(conn);
        broadcast(userName + " has left the chat room.");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String[] command = message.split(" ", 2);
        switch (command[0].toLowerCase()) {
            case "join_room":
                String username = command[1];
                userNames.put(conn, username);
                broadcast(username + " has joined the chat!");
                break;
            case "send_msg":
                if (command.length > 1) {
                    String userName = userNames.getOrDefault(conn, "A user");
                    String messageText = userName + ": " + command[1];
                    broadcast(messageText);
                    messageHistory.add(messageText); // Add the message to the history
                } else {
                    conn.send("Usage: send_msg <message>");
                }
                break;
            case "leave_room":
                conn.close();
                break;
            default:
                conn.send("Unknown command. Use 'join_room', 'send_msg <message>', 'leave_room'.");
                break;
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket chat server started on port: " + PORT);
    }

    public void broadcast(String message) {
        for (WebSocket client : clients) {
            client.send(message);
        }
    }

    public static void main(String[] args) {
        ChatWebSocketServer server = new ChatWebSocketServer();
        server.start();
        System.out.println("WebSocket chat server started on port: " + PORT);
    }
}
