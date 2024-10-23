package org.example.Laboratory_work_2.connect_db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnect {

    public DBconnect(String host, String user, String pass) throws SQLException {
        Connection connection = DriverManager.getConnection(host, user, pass);
    }

}
