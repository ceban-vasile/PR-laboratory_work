package org.example.Laboratory_work_2;

import org.example.Laboratory_work_2.operations.CRUDOperation;
import org.example.Laboratory_work_2.server.HTTPServer;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        HTTPServer runServer = new HTTPServer();

        try {
            runServer.runServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
