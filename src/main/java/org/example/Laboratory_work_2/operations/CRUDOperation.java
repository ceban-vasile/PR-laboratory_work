package org.example.Laboratory_work_2.operations;

import org.example.Laboratory_work_2.interface_connect_db.Connect_DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CRUDOperation implements Connect_DB {
    public CRUDOperation(){}

    private void insertProductToDB(String name, String color, double price, String currency, String time, String link) throws SQLException {

        //Connect to database
        Connection connect = connect();

        //create query to insert a product
        String sql = "INSERT INTO products(name, color, price, currency, time, link) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connect.prepareStatement(sql);

        preparedStatement.setString(1, name);
        preparedStatement.setString(2, color);
        preparedStatement.setDouble(3, price);
        preparedStatement.setString(4, currency);
        preparedStatement.setString(5, time);
        preparedStatement.setString(6, link);

        int rowsInserted = preparedStatement.executeUpdate();
        System.out.println(rowsInserted + " row(s) inserted.");

        preparedStatement.close();
        connect.close();
    }

    @Override
    public Connection connect() {
        Connection conn = null;
        try {
            DriverManager.getConnection("jdbc:postgresql://localhost:5433/data_product", "postgres", "admin1234");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
}
