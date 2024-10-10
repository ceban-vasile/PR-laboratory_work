package org.example.services;

import org.example.Model.Product;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public class Processor {

    public void convertCurrency(List<Product> products) {
        products.stream()
                .map(product -> {
                    if (product.getCurrency().equals("£")) {
                        product.setPrice(product.getPrice() * 20.03);
                        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
                        product.setCurrency("£/mdl");
                        product.setTime(String.valueOf(now));
                    }
                    return product;
                })
                .collect(Collectors.toList());
    }

    public List<Product> filterByPriceRange(List<Product> products, int min, int max) {
        return products.stream()
                .filter(product -> product.getPrice() >= min && product.getPrice() <= max)
                .collect(Collectors.toList());
    }

    public Double sumPrices(List<Product> products) {
        Double sum = 0.0;
        for (Product product : products) {
            Double price = product.getPrice();
            sum += price;
        }
        return sum;
    }
}
