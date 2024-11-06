package org.example.Laboratory_work_2.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Laboratory_work_1.Model.Product;
import org.example.Laboratory_work_2.operations.CRUDOperation;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HTTPHandler implements HttpHandler {

    public HTTPHandler() {}

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {

            byte[] body = exchange.getRequestBody().readAllBytes();
            String requestBody = new String(body, StandardCharsets.UTF_8);

            JSONObject json = new JSONObject(requestBody);
            String name = json.getString("name");
            String color = json.getString("color");
            double price = json.getDouble("price");
            String currency = json.getString("currency");
            String time_converted = json.getString("time_converted");
            String link = json.getString("link");

            try {
                new CRUDOperation().insertProductToDB(name, color, price, currency, time_converted, link);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String response = "Product added successfully!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }else if("DELETE".equals(exchange.getRequestMethod())){

            byte[] body = exchange.getRequestBody().readAllBytes();
            String requestBody = new String(body, StandardCharsets.UTF_8);

            JSONObject json = new JSONObject(requestBody);
            String name = json.getString("name");

            try {
                new CRUDOperation().deleteProductToDB(name);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String response = "Product deleted successfully!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }else if("PUT".equals(exchange.getRequestMethod())){

            byte[] body = exchange.getRequestBody().readAllBytes();
            String requestBody = new String(body, StandardCharsets.UTF_8);

            JSONObject json = new JSONObject(requestBody);
            String name = json.getString("name");
            double price = json.getDouble("price");

            try {
                new CRUDOperation().updateProductToDB(name, price);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String response = "Product update successfully!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }else if("GET".equals(exchange.getRequestMethod())){
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQueryParams(query);

            int offset = Integer.parseInt(params.getOrDefault("offset", "0"));
            int limit = Integer.parseInt(params.getOrDefault("limit", "5"));

            String response = "";
            try {
                List<Product> products = new CRUDOperation().displayProductsToDB(offset, limit);
                Gson gson = new Gson();
                response = gson.toJson(products);  // Convert product list to JSON
            } catch (SQLException e) {
                e.printStackTrace();
                response = "Database error: " + e.getMessage();
            }

            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        return query == null ? Map.of() :
                Arrays.stream(query.split("&"))
                        .map(param -> param.split("="))
                        .collect(Collectors.toMap(p -> p[0], p -> p[1]));
    }

}
