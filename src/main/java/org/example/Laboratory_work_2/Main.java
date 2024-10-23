package org.example.Laboratory_work_2;

import org.example.Laboratory_work_2.connect_db.DBconnect;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DBconnect dBconnect = new DBconnect("jdbc:postgresql://localhost:5433/data_product", "postgres", "admin1234");
    }
}
