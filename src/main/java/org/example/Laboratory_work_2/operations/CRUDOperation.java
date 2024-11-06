package org.example.Laboratory_work_2.operations;

import org.example.Laboratory_work_1.Model.Product;
import org.example.Laboratory_work_2.connect_db.Connect_DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CRUDOperation implements Connect_DB {

    public CRUDOperation(){}

    public void insertProductToDB(String name, String color, double price, String currency, String time, String link) throws SQLException {

        Connection connect = connect();

        String sql = "INSERT INTO products(name, color, price, currency, time_convert, link) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connect.prepareStatement(sql);

        preparedStatement.setString(1, name);
        preparedStatement.setString(2, color);
        preparedStatement.setDouble(3, price);
        preparedStatement.setString(4, currency);
        preparedStatement.setString(5, time);
        preparedStatement.setString(6, link);

        int rowsInserted = preparedStatement.executeUpdate();
        System.out.println(rowsInserted + " row inserted.");

        preparedStatement.close();
        connect.close();
    }

    public void updateProductToDB(String name, double price) throws SQLException {

        Connection connect = connect();

        String sql = "UPDATE products SET price=? WHERE name=?";
        PreparedStatement preparedStatement = connect.prepareStatement(sql);

        preparedStatement.setString(2, name);
        preparedStatement.setDouble(1, price);

        int rowsUpdated = preparedStatement.executeUpdate();
        System.out.println(rowsUpdated + " row updated.");

        connect.close();
        preparedStatement.close();
    }

    public void deleteProductToDB(String name) throws SQLException {

        Connection connect = connect();

        String sql = "DELETE FROM products WHERE name = ?";

        PreparedStatement preparedStatement = connect.prepareStatement(sql);

        preparedStatement.setString(1, name);

        int rowsUpdated = preparedStatement.executeUpdate();
        System.out.println(rowsUpdated + " row deleted.");

        connect.close();
        preparedStatement.close();

    }

    public List<Product> displayProductsToDB(int offset, int limit) throws SQLException {

        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products ORDER BY id LIMIT ? OFFSET ?";

        try (Connection connect = connect();
             PreparedStatement preparedStatement = connect.prepareStatement(sql)) {

            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                products.add(new Product(
                        resultSet.getString("name"),
                        resultSet.getString("color"),
                        resultSet.getDouble("price"),
                        resultSet.getString("currency"),
                        resultSet.getString("time_convert"),
                        resultSet.getString("link")
                ));
            }
        }

        return products;
    }

    @Override
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5433/data_product",
                    "postgres",
                    "admin1234"
            );
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
        return conn;
    }
}
