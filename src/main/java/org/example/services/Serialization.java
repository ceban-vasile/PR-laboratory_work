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
        return "<Product>\n" +
                "    <name>" + product.getName() + "</name>\n" +
                "    <color>" + product.getColor() + "</color>\n" +
                "    <price>" + product.getPrice() + "</price>\n" +
                "    <currency>" + product.getCurrency() + "</currency>\n" +
                "    <currentConversionTime>" + product.getTime() + "</currentConversionTime>\n" +
                "    <link>" + product.getLink() + "</link>\n" +
                "</Product>";
    }

    public String serialize(Product product) {
        return "name:" + product.getName() + "|" +
                "color:" + product.getColor() + "|" +
                "price:" + product.getPrice() + "|" +
                "currency:" + product.getCurrency() + "|" +
                "time:" + product.getTime() + "|" +
                "link:" + product.getLink();
    }

    public Product deserialize(String data) {
        String[] fields = data.split("\\|");
        String name = fields[0].split(":")[1];
        String color = fields[1].split(":")[1];
        double price = Double.parseDouble(fields[2].split(":")[1]);
        String currency = fields[3].split(":")[1];
        String time = fields[4].split(":")[1];
        String link = fields[5].split(":")[1];

        return new Product(name, color, price, currency, time, link);
    }


}
