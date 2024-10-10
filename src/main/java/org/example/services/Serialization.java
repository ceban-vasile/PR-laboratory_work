package org.example.services;
import org.example.Model.Product;

public class Serialization {

    public String serializeToJson(Product product) {
        return "{\n" +
                "\"name\": \"" + product.getName() + "\",\n" +
                "\"color\": \"" + product.getColor() + "\",\n" +
                "\"price\": " + product.getPrice() + ",\n" +
                "\"currency\": " + product.getCurrency() + ",\n" +
                "\"current conversion time:\": " + product.getTime() + ",\n" +
                "\"link\": \"" + product.getLink() + "\"\n" +
                "}";
    }

    public String serializeToXml(Product product) {
        return "<product>" +
                "<name>" + product.getName() + "</name>" +
                "<price>" + product.getPrice() + "</price>" +
                "<color>" + product.getColor() + "</color>" +
                "</product>";
    }
}
