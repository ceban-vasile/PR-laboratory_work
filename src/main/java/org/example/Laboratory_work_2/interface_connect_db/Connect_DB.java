package org.example.Laboratory_work_2.interface_connect_db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface Connect_DB {
    Connection connect();
}
