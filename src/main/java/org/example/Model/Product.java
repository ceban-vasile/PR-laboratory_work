package org.example.Model;

public class Product {

    String name;
    Double price;
    String color;
    String currency;
    String time;
    String link;

    public Product(String name, String color, double price, String currency, String time, String link) {}

    public Product(String name, Double price, String currency, String link) {
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLink() {
        return link;
    }
}
